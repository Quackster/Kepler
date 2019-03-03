#include "list.h"
#include "roller_task.h"

#include "game/pathfinder/coord.h"

#include "game/room/room_user.h"
#include "game/room/mapping/room_map.h"
#include "game/room/mapping/room_tile.h"

#include "game/player/player.h"
#include "database/queries/items/item_query.h"

#include "util/stringbuilder.h"

/**
 * Task called for rollers inside room.
 *
 * @param room the room to call the task for
 */
void roller_task(room *room) {
    if (room == NULL) {
        return;
    }

    HashTable *blacklist;
    hashtable_new(&blacklist);

    bool regenerate_map = false;

    for (size_t roller_index = 0; roller_index < list_size(room->items); roller_index++) {
        item *roller;
        list_get_at(room->items, roller_index, (void *) &roller);

        if (!roller->definition->behaviour->is_roller) {
            continue;
        }

        room_tile *item_tile = room->room_map->map[roller->position->x][roller->position->y];

        if (item_tile == NULL) {
            continue;
        }

        for (size_t item_index = 0; item_index < list_size(item_tile->items); item_index++) {
            item *item;
            list_get_at(item_tile->items, item_index, (void *) &item);

            if (do_roller_item(room, roller, item)) {
                regenerate_map = true;
            }
        }

        room_user *room_entity = item_tile->entity;

        if (room_entity != NULL) {
            if (room_entity->entity->entity_type == PLAYER_TYPE) {
                if (!hashtable_contains_key(blacklist, &room_entity->entity->details->id)) {
                    hashtable_add(blacklist, &room_entity->entity->details->id, room_entity);

                    do_roller_player(room, roller, room_entity);
                }
            }
        }
    }

    if (regenerate_map) {
        room_map_regenerate(room);
    }

    hashtable_destroy(blacklist);
}

/**
 * Handle rolling item on top of roller.
 *
 * @param room the room the item is rolling in
 * @param roller the roller being utilised
 * @param item the item that is rolling
 * @return true, if item rolled.
 */
bool do_roller_item(room *room, item *roller, item *item) {
    if (item->id == roller->id) {
        return false;
    }

    if (item->position->z < roller->position->z) {
        return false;
    }

    if (item->definition->behaviour->is_roller) {
        return false;
    }

    if (item->definition->length > 1 || item->definition->width > 1) {
        return false;
    }

    coord to;
    coord_get_front(roller->position, &to);

    coord from;
    from.x = item->position->x;
    from.y = item->position->y;
    from.z = item->position->z;

    if (!room_tile_is_walkable(room, NULL, to.x, to.y)) {
        return false;
    }

    room_tile *front_tile = room->room_map->map[to.x][to.y];
    double next_height = front_tile->tile_height;

    if (front_tile->highest_item != NULL) {
        if (!front_tile->highest_item->definition->behaviour->is_roller) {
            if (item->definition->behaviour->can_stack_on_top && item->definition->stack_height == front_tile->highest_item->definition->stack_height) {
                next_height -= item->definition->stack_height;
            }
        }
    }

    if (item->item_below != NULL) {
        if (!item->item_below->definition->behaviour->is_roller) {
            next_height = item->position->z;

            bool subtract_roller_height = false;

            if (front_tile->highest_item != NULL) {
                if (!front_tile->highest_item->definition->behaviour->is_roller) {
                    subtract_roller_height = true;
                }
            } else {
                subtract_roller_height = true;
            }

            if (subtract_roller_height) {
                next_height -= roller->definition->stack_height;
            }
        }
    }

    to.z = next_height;

    item->position->x = to.x;
    item->position->y = to.y;
    item->position->z = to.z;

    outgoing_message *om = om_create(230); // "Cf"
    om_write_int(om, from.x);
    om_write_int(om, from.y);
    om_write_int(om, to.x);
    om_write_int(om, to.y);
    om_write_int(om, 1);
    om_write_int(om, item->id);
    sb_add_float_delimeter(om->sb, from.z, 2);
    sb_add_float_delimeter(om->sb, to.z, 2);
    om_write_int(om, roller->id);
    room_send(room, om);
    om_cleanup(om);

    item_query_save(item);
    return true;
}

/**
 * Handle rolling player on top of roller.
 *
 * @param room the room the item is rolling in
 * @param roller the roller being utilised
 * @param room_entity the entity that is rolling
 */
void do_roller_player(room *room, item *roller, room_user *room_entity) {
    if (room_entity->is_walking) {
        return;
    }

    if (room_entity->position->z < roller->position->z) {
        return;
    }

    if (room_entity->room == NULL) {
        return;
    }

    coord to;
    coord_get_front(roller->position, &to);

    if (!room_tile_is_walkable(room, room_entity, to.x, to.y)) {
        return;
    }

    coord from;
    from.x = room_entity->position->x;
    from.y = room_entity->position->y;

    room_tile *previous_tile = room->room_map->map[from.x][from.y];
    room_tile *next_tile = room->room_map->map[to.x][to.y];

    to.z = next_tile->tile_height;

    room_user_invoke_item(room_entity, false);
    room_entity->position->x = to.x;
    room_entity->position->y = to.y;
    room_entity->position->z = to.z;
    room_entity->needs_update_from_secs = 3;

    outgoing_message *om = om_create(230); // "Cf"
    om_write_int(om, from.x);
    om_write_int(om, from.y);
    om_write_int(om, to.x);
    om_write_int(om, to.y);
    om_write_int(om, 0);
    om_write_int(om, roller->id);
    om_write_int(om, 2);
    om_write_int(om, room_entity->instance_id);
    sb_add_float_delimeter(om->sb, from.z, 2);
    sb_add_float_delimeter(om->sb, to.z, 2);
    room_send(room, om);
    om_cleanup(om);

    previous_tile->entity = NULL;
    next_tile->entity = room_entity;
}
