#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "list.h"

#include "item.h"
#include "item_manager.h"

#include "game/pathfinder/coord.h"
#include "game/pathfinder/affected_tiles.h"

#include "game/room/room.h"
#include "game/room/room_user.h"
#include "game/room/room_manager.h"

#include "game/room/mapping/room_tile.h"
#include "game/room/mapping/room_map.h"
#include "game/room/mapping/room_model.h"

#include "communication/messages/outgoing_message.h"
#include "util/stringbuilder.h"

/**
 * Create room item struct.
 *
 * @param room_id the room id of the item
 * @param class_name the item class name
 * @param sprite_id the item sprite name
 * @param x the item x coordinate
 * @param y the item y coordinate
 * @param z the item z coordinate
 * @param rotation the item rotation
 * @param custom_data the item custom data
 * @return
 */
item *item_create(int id, int room_id, int owner_id, int definition_id, int x, int y, double z, char *wall_position, int rotation, char *custom_data) {
    item *room_item = malloc(sizeof(item));
    room_item->id = id;
    room_item->room_id = room_id;
    room_item->owner_id = owner_id;

    if (definition_id > 0) {
        room_item->definition = item_manager_get_definition_by_id(definition_id);
    }

    if (custom_data == NULL) {
        custom_data = strdup("");
    }

    room_item->position = create_coord_height(x, y, z);
    room_item->item_below = NULL;
    room_item->custom_data = custom_data;
    room_item->current_program = NULL;
    room_item->current_program_state = NULL;
    room_item->position->rotation = rotation;
    room_item->wall_position = wall_position;

    return room_item;
}

/**
 * Update user statuses on items with their old position and new position.
 *
 * @param item the item to update for
 * @param room the room to update inside
 * @param old_position the old position of the item
 */
void item_update_entities(item *item, room *room, coord *old_position) {
    if (item->definition->behaviour->is_wall_item) {
        return;
    }

    List *entities_to_update;
    list_new(&entities_to_update);

    // Do old position updates
    if (old_position != NULL) {
        List *affected_tiles = get_affected_tiles(item->definition->length, item->definition->width, old_position->x, old_position->y, old_position->rotation);

        for (size_t i = 0; i < list_size(affected_tiles); i++) {
            coord *pos;
            list_get_at(affected_tiles, i, (void *) &pos);

            room_tile *tile = room->room_map->map[pos->x][pos->y];

            if (tile != NULL && tile->entity != NULL) {
                list_add(entities_to_update, tile->entity);
            }

            free(pos);
        }

        list_destroy(affected_tiles);
    }

    // Do new position updates
    List *new_affected_tiles = get_affected_tiles(item->definition->length, item->definition->width, item->position->x, item->position->y, item->position->rotation);

    for (size_t i = 0; i < list_size(new_affected_tiles); i++) {
        coord *pos;
        list_get_at(new_affected_tiles, i, (void *) &pos);

        room_tile *tile = room->room_map->map[pos->x][pos->y];

        if (tile != NULL && tile->entity != NULL) {
            list_add(entities_to_update, tile->entity);
        }

        free(pos);
    }

    list_destroy(new_affected_tiles);

    for (size_t i = 0; i < list_size(entities_to_update); i++) {
        room_user *room_user;
        list_get_at(entities_to_update, i, (void*)&room_user);

        room_user_invoke_item(room_user, true);
    }

    list_destroy(entities_to_update);
}

/**
 * Override the old custom data on the item with new custom data.
 *
 * @param item the item to set the custom data for
 * @param custom_data the new custom data
 */
void item_set_custom_data(item *item, char *custom_data) {
    if (item->custom_data != NULL) {
        free(item->custom_data);
        item->custom_data = NULL;
    }

    item->custom_data = custom_data;
}

/**
 * Broadcast custom data to the room, doesn't apply it to the item.
 *
 * @param item the item
 * @param custom_data the custom data
 */
void item_broadcast_custom_data(item* item, char *custom_data) {
    outgoing_message *om;

    if (item->definition->behaviour->is_wall_item) {
        om = om_create(85); // "AU"
        char *item_string = item_as_string(item);
        sb_add_string(om->sb, item_string);
        free(item_string);
    } else {
        om = om_create(88); // "AX"
        sb_add_int_delimeter(om->sb, item->id, 2);
        sb_add_string_delimeter(om->sb, item->custom_data, 2);
        sb_add_string_delimeter(om->sb, "", 2);
    }

    room *room = room_manager_get_by_id(item->room_id);

    if (room != NULL) {
        room_send(room, om);
    }

    om_cleanup(om);
}

/**
 * Gets whether or not the item is walkable.
 *
 * @param item the item to check
 * @return true, if successful
 */
bool item_is_walkable(item *item) {
    if (item->definition->behaviour->can_lay_on_top) {
        return true;
    }

    if (item->definition->behaviour->can_sit_on_top) {
        return true;
    }

    if (item->definition->behaviour->can_stand_on_top) {
        return true;
    }

    if (item->definition->behaviour->is_door) {
        return strcmp(item->custom_data, "O") == 0;
    }

    return false;
}

/**
 * Get the item represented as a string for various item packets.
 * Must be free'd after use.
 *
 * @param item the item to get the string for
 * @return the string representation of the time.
 */
char *item_as_string(item *item) {
    stringbuilder *sb = sb_create();

    if (item->definition->behaviour->is_wall_item) {
        sb_add_int_delimeter(sb, item->id, 9);
        sb_add_string_delimeter(sb, item->definition->sprite, 9);
        sb_add_string_delimeter(sb, " ", 9);
        sb_add_string_delimeter(sb, item->wall_position, 9);

        if (item->custom_data != NULL) {
            if (item->definition->behaviour->is_post_it) {
                char color[7];
                memcpy(color, &item->custom_data[0], 6);
                color[6] = '\0';

                sb_add_string(sb, color);
            } else {
                sb_add_string(sb, item->custom_data);
            }
        }

    } else {
        if (!item->definition->behaviour->is_public_space_object) {
            sb_add_int_delimeter(sb, item->id, 2);
            sb_add_string_delimeter(sb, item->definition->sprite, 2);
            sb_add_wired(sb, item->position->x);
            sb_add_wired(sb, item->position->y);
            sb_add_wired(sb, item->definition->length);
            sb_add_wired(sb, item->definition->width);
            sb_add_wired(sb, item->position->rotation);
            sb_add_float_delimeter(sb, item->position->z, 2);
            sb_add_string_delimeter(sb, item->definition->colour, 2);
            sb_add_string_delimeter(sb, "", 2);
            sb_add_wired(sb, 0);
            sb_add_string_delimeter(sb, item->custom_data, 2);
        } else {
            sb_add_string_delimeter(sb, item->custom_data, ' ');
            sb_add_string_delimeter(sb, item->definition->sprite, ' ');
            sb_add_int_delimeter(sb, item->position->x, ' ');
            sb_add_int_delimeter(sb, item->position->y, ' ');
            sb_add_int_delimeter(sb, (int) item->position->z, ' ');
            sb_add_int(sb, item->position->rotation);

            if (item->definition->behaviour->has_extra_parameter) {
                sb_add_string(sb, " 2");
            }

            sb_add_char(sb, 13);
        }
    }

    char *str = strdup(sb->data);
    sb_cleanup(sb);

    return str;
}


/**
 * Assign program, used for many public rooms.
 *
 * @param room_item the item to assign
 * @param program_state the state of the item
 */
void item_assign_program(item *room_item, char *program_state) {
    if (room_item->current_program_state != NULL) {
        free(room_item->current_program_state);
    }

    if (program_state != NULL) {
        room_item->current_program_state = strdup(program_state);

        room *room = room_manager_get_by_id(room_item->room_id);

        if (room != NULL) {
            outgoing_message *om = om_create(71); // "AG"
            sb_add_string(om->sb, room_item->current_program);

            if (strlen(room_item->current_program_state) > 0) {
                sb_add_string(om->sb, " ");
                sb_add_string(om->sb, room_item->current_program_state);
            }

            room_send(room, om);
            om_cleanup(om);
        }

    } else {
        room_item->current_program_state = NULL;
    }
}

/**
 * Gets the total height of the item, the height of the item itself
 * plus the stack height.
 *
 * @param item the item to get the height for
 * @return the total height
 */
double item_total_height(item *item) {
    double height = item->position->z + item->definition->stack_height;
    return height;
}

/**
 * Dispose item.
 *
 * @param item the item to dispose
 */
void item_dispose(item *item) {
    free(item->custom_data);
    free(item->position);

    if (item->wall_position != NULL) {
        free(item->wall_position);
    }

    if (item->current_program != NULL) {
        free(item->current_program);
    }

    if (item->current_program_state != NULL) {
        free(item->current_program_state);
    }

    room *room = room_manager_get_by_id(item->room_id);

    if (room != NULL && item->room_id > 0) {
        if (room->room_data->owner_id == 0) {
            item_definition_dispose(item->definition); // Destroy if public item since every public item has their own definition
        }
    }

    free(item);
}