#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/rooms/room_rights_query.h"

void ASSIGNRIGHTS(entity *player, incoming_message *im) {
    if (player->room_user->room == NULL) {
        return;
    }

    room *room = player->room_user->room;

    if (!room_is_owner(room, player->details->id)) {
        return;
    }

    char *username = im_get_content(im);

    if (username == NULL) {
        return;
    }

    int user_id = player_query_id(username);

    if (user_id == -1) {
        goto cleanup;
    }

    if (!rights_entry_find(room, user_id)) {
        rights_entry *entry = rights_entry_create(user_id);
        list_add(room->rights, entry);

        room_refresh_rights(room, player_manager_find_by_id(user_id));
        room_query_add_rights(room->room_id, user_id);
    }

    cleanup:
    free(username);
}
