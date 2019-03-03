#include <game/player/player.h>
#include "stdlib.h"
#include "stdio.h"

#include "list.h"
#include "database/queries/items/item_query.h"

#include "room_tile.h"
#include "room_map.h"

#include "game/player/player.h"

#include "game/pathfinder/coord.h"
#include "game/pathfinder/affected_tiles.h"

#include "game/items/item.h"
#include "game/items/definition/item_definition.h"

#include "game/room/room.h"
#include "game/room/room_user.h"
#include "game/room/mapping/room_model.h"
#include "game/room/public_rooms/pool_handler.h"

#include "communication/messages/outgoing_message.h"
#include "util/stringbuilder.h"

void room_map_add_items(room *room);

/**
 * Sort list by item heights. Lowest height items come first.
 *
 * @param e1 the first item
 * @param e2 the second item
 * @return whether to sort
 */
int cmp(void const *e1, void const *e2) {
    item *i = (*((item**) e1));
    item *j = (*((item**) e2));

    if (i->position->z < j->position->z)
        return -1;
    if (i->position->z == j->position->z)
        return 0;

    return 1;
}

/**
 * Initalises the room map for the furniture collision.
 *
 * @param room the room instance
 */
void room_map_init(room *room) {
    if (room->room_map == NULL) {
        room->room_map = malloc(sizeof(room_map));

        for (int x = 0; x < room->room_data->model_data->map_size_x; x++) {
            for (int y = 0; y < room->room_data->model_data->map_size_y; y++) {
                room->room_map->map[x][y] = room_tile_create(room, x, y);
            }
        }
    }

    room_map_regenerate(room);
}
/**
 * Regenerates the room map.
 *
 * @param room the room instance
 */
void room_map_regenerate(room *room) {
    // Reset all tiles
    for (int x = 0; x < room->room_data->model_data->map_size_x; x++) { 
        for (int y = 0; y < room->room_data->model_data->map_size_y; y++) { 
            room_tile *tile = room->room_map->map[x][y];
            room_tile_reset(tile, room);
        }
    }

    // Add players to tiles
    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *room_player;
        list_get_at(room->users, i, (void *) &room_player);

        room_tile *tile = room->room_map->map[room_player->room_user->position->x][room_player->room_user->position->y];

        if (tile == NULL) {
            continue;
        }

        tile->entity = room_player->room_user;
    }

    room_map_add_items(room);
}


/**
 * Add items to the room collision map.
 *
 * @param room the room instance
 */
void room_map_add_items(room *room) {
    List *items;
    list_copy_shallow(room->items, &items);
    list_sort_in_place(items, cmp);

    for (size_t item_index = 0; item_index < list_size(items); item_index++) {
        item *item;
        list_get_at(items, item_index, (void*)&item);

        if (item->definition->behaviour->is_wall_item) {
            continue;
        }

        item->item_below = NULL;

        room_tile *tile = room->room_map->map[item->position->x][item->position->y];

        if (tile == NULL) {
            continue;
        }

        room_tile_add_item(tile, item);

        if (item->definition->behaviour->is_public_space_object
            || tile->tile_height < item_total_height(item)) {

            item->item_below = tile->highest_item;
            tile->tile_height = item_total_height(item);
            tile->highest_item = item;

            List *affected_tiles = get_affected_tiles(item->definition->length, item->definition->width, item->position->x, item->position->y, item->position->rotation);

            for (size_t i = 0; i < list_size(affected_tiles); i++) {
                coord *pos;
                list_get_at(affected_tiles, i, (void*)&pos);

                if (pos->x == item->position->x && pos->y == item->position->y) {
                    goto remove_tile;
                }

                room_tile *affected_tile = room->room_map->map[pos->x][pos->y];

                if (affected_tile == NULL) {
                    goto remove_tile;
                }

                affected_tile->tile_height = item_total_height(item);
                affected_tile->highest_item = item;

                remove_tile:
                free(pos);
            }

            list_destroy(affected_tiles);
        }

        if (item->definition->behaviour->is_public_space_object) {
            pool_setup_redirections(room, item);
        }
    }

    list_destroy(items);
}

/**
 * Add a specific item to the room map
 *
 * @param room the room to add the map item to
 * @param item the item to add
 */
void room_map_add_item(room *room, item *item) {
    item->room_id = room->room_id;
    list_add(room->items, item);

    if (item->definition->behaviour->is_wall_item) {
        char *item_str = item_as_string(item);

        outgoing_message *om = om_create(83); // "AS"
        sb_add_string(om->sb, item_str);
        room_send(room, om);
        om_cleanup(om);

        free(item_str);
    } else {
        room_map_item_adjustment(room, item, false);
        room_map_regenerate(room);

        char *item_str = item_as_string(item);

        outgoing_message *om = om_create(93); // "A]"
        sb_add_string(om->sb, item_str);
        room_send(room, om);
        om_cleanup(om);

        free(item_str);
    }

    item_update_entities(item, room, NULL);
    item_query_save(item);
}

/**
 * Move an item, will regenerate the map if the item is a floor item.
 *
 * @param room the room the item is in
 * @param item the item that is moving
 * @param rotation whether it's just rotation or not
 *        (don't regenerate map or adjust position if it's just a rotation)
 */
void room_map_move_item(room *room, item *item, bool rotation, coord *old_position) {
    item->room_id = room->room_id;

    if (!item->definition->behaviour->is_wall_item) {
        room_map_item_adjustment(room, item, rotation);
        room_map_regenerate(room);

        char *item_str = item_as_string(item);

        outgoing_message *om = om_create(95); // "A_"
        sb_add_string(om->sb, item_str);
        room_send(room, om);
        om_cleanup(om);

        free(item_str);
    }

    item_update_entities(item, room, old_position);
    item_query_save(item);
}

/**
 * Remove an item from the room.
 *
 * @param room the room to remove the item from
 * @param item the item that is being removed
 */
void room_map_remove_item(room *room, item *item) {
    list_remove(room->items, item, NULL);

    if (item->definition->behaviour->is_wall_item) {
        outgoing_message *om = om_create(84); // "AT"
        sb_add_int(om->sb, item->id);
        room_send(room, om);
        om_cleanup(om);
    } else {
        room_map_regenerate(room);

        char *item_str = item_as_string(item);

        outgoing_message *om = om_create(94); // "A^"
        sb_add_string(om->sb, item_str);
        room_send(room, om);
        om_cleanup(om);

        free(item_str);
    }

    item_update_entities(item, room, NULL);

    item->room_id = 0;
    item->position->x = 0;
    item->position->y = 0;
    item->position->z = 0;
    item_query_save(item);
}

/**
 * Handle item adjustment.
 *
 * @param moveItem the item
 * @param rotation the rotation only
 */
void room_map_item_adjustment(room *room, item *adjusted_item, bool rotation) {
    room_tile *tile = room->room_map->map[adjusted_item->position->x][adjusted_item->position->y];

    if (tile == NULL) {
        return;
    }

    if (rotation) {
        /*for (size_t i = 0; i < list_size(tile->items); i++) {
            item *item;
            list_get_at(tile->items, i, (void *) &item);

            if (item->id == adjusted_item->id) {
                continue;
            }

            if (item->position->z < adjusted_item->position->z) {
                continue;
            }

            // Set rotation of the entire tile stack
            item->position->rotation = adjusted_item->position->rotation;

            // Update rooms users
            char *item_str = item_as_string(item);
            outgoing_message *om = om_create(95); // "A_"
            sb_add_string(om->sb, item_str);
            room_send(rooms, om);
            free(item_str);
        }*/
   } else {
        adjusted_item->position->z = tile->tile_height;
    }

    if (adjusted_item->position->z > 8) {
        adjusted_item->position->z = 8;
    }
}

/**
 * Destroy the room map instance.
 *
 * @param room the room instance
 */
void room_map_destroy(room *room) {
    if (room->room_map != NULL) {
        for (int x = 0; x < room->room_data->model_data->map_size_x; x++) {
            for (int y = 0; y < room->room_data->model_data->map_size_y; y++) {
                room_tile_destroy(room->room_map->map[x][y], room);
                room->room_map->map[x][y] = NULL;
            }
        }

        free(room->room_map);
        room->room_map = NULL;
    }

}