#include <shared.h>

#include "list.h"

#include "room_item_manager.h"

#include "game/items/item.h"
#include "game/room/room.h"
#include "game/items/definition/item_definition.h"

#include "database/queries/items/item_query.h"

List *room_item_manager_floor_items(room *room) {
    List *items;
    list_new(&items);

    for (size_t i = 0; i < list_size(room->items); i++) {
        item *room_item;
        list_get_at(room->items, i, (void *) &room_item);

        if (room_item->definition->behaviour->is_wall_item) {
            continue;
        }

        if (room_item->definition->behaviour->is_public_space_object) {
            continue;
        }

        list_add(items, room_item);
    }

    return items;
}

List *room_item_manager_wall_items(room *room) {
    List *items;
    list_new(&items);

    for (size_t i = 0; i < list_size(room->items); i++) {
        item *room_item;
        list_get_at(room->items, i, (void *) &room_item);

        if (room_item->definition->behaviour->is_wall_item) {
            list_add(items, room_item);
        }
    }

    return items;
}
List *room_item_manager_public_items(room *room) {
    List *items;
    list_new(&items);

    for (size_t i = 0; i < list_size(room->items); i++) {
        item *room_item;
        list_get_at(room->items, i, (void *) &room_item);

        if (room_item->definition->behaviour->is_public_space_object) {
            list_add(items, room_item);
        }
    }

    return items;
}

item *room_item_manager_get(room *room, int item_id) {
    for (size_t i = 0; i < list_size(room->items); i++) {
        item *item;
        list_get_at(room->items, i, (void*)&item);

        if (item->id == item_id) {
            return item;
        }
    }

    return NULL;
}

void room_item_manager_load(room *room) {
    if (room->room_data->owner_id == 0) {
        return;
    }

    List *items = item_query_get_room_items(room->room_id);

    for (size_t i = 0; i < list_size(items); i++) {
        item *item;
        list_get_at(items, i, (void*)&item);
        list_add(room->items, item);
    }

    list_destroy(items);
}

void room_item_manager_dispose(room *room) {
    for (size_t i = 0; i < list_size(room->items); i++) {
        item *item;
        list_get_at(room->items, i, (void*)&item);
        item_dispose(item);
    }

    list_remove_all(room->items);
}