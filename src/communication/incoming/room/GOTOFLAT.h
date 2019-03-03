#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void GOTOFLAT(entity *player, incoming_message *message) {
    char *content = im_get_content(message);

    if (!is_numeric(content)) {
        goto cleanup;
    }

    int room_id = (int)strtol(content, NULL, 10);
    room *room = room_manager_get_by_id(room_id);

    if (player->room_user->authenticate_id != room_id) {
        goto cleanup;
    }

    if (room == NULL) {
        room = room_query_get_by_room_id(room_id);

        if (room != NULL) {
            hashtable_add(global.room_manager.rooms, &room->room_id, room);
        }
    }

    if (room == NULL) {
        goto cleanup;
    }

    room_enter(room, player, NULL);

    cleanup:
    free(content);
}
