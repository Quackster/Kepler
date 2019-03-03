#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/rooms/room_rights_query.h"

void REMOVERIGHTS(entity *player, incoming_message *im) {
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


    rights_entry *entry = rights_entry_find(room, user_id);

    if (entry != NULL) {
        list_remove(room->rights, entry, NULL);

        entity *to_remove = player_manager_find_by_id(user_id);

        if (to_remove->room_user->room_id == room->room_id) {
            room_user_remove_status(to_remove->room_user, "flatctrl");
            to_remove->room_user->needs_update = true;

            outgoing_message *om = om_create(43); // "@k"
            player_send(to_remove, om);
            om_cleanup(om);
        }

        room_query_remove_rights(room->room_id, user_id);
    }

    cleanup:
    free(username);
}
