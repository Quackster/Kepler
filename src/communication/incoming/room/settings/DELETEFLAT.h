#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void delete_room_items(List *items);

void DELETEFLAT(entity *player, incoming_message *message) {
    char *content = im_get_content(message);
    bool dispose_after = false;

    if (!is_numeric(content)) {
        goto cleanup;
    }

    int room_id = (int) strtol(content, NULL, 10);
    room *room = room_manager_get_by_id(room_id);

    if (room == NULL) {
        dispose_after = true;
        room = room_query_get_by_room_id(room_id);
    }

    if (room == NULL) {
        goto cleanup;
    }

    if (room_is_owner(room, player->details->id)) {
        room_kickall(room);

        if (list_size(room->items) <= 0) {
            room_item_manager_load(room);
        }

        delete_room_items(room->items);

        room_dispose(room, true);
        room_query_delete(room_id);
    }

    cleanup:
    free(content);
}

void delete_room_items(List *items) {
    if (list_size(items) > 0) {
        for (size_t i = 0; i < list_size(items); i++) {
            item *item;
            list_get_at(items, i, (void *) &item);

            item_manager_delete(item);
        }

        list_remove_all(items);
    }
}
