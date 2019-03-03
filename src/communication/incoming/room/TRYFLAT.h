#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/room/room.h"
#include "game/room/room_user.h"
#include "game/room/room_manager.h"

bool ring_doorbell_alerted(room *room, entity *ringing);

void TRYFLAT(entity *player, incoming_message *message) {
    int room_id = 0;

    char *content = im_get_content(message);
    char *password = NULL;

    if (is_numeric(content)) {
        room_id = (int) strtol(content, NULL, 10);
    } else {
        char *temp = get_argument(content, "/", 0);

        if (!is_numeric(temp)) {
            free(temp);
            goto cleanup;
        }

        room_id = (int) strtol(content, NULL, 10);
        password = get_argument(content, "/", 1);

        free(temp);
    }

    room *room = room_manager_get_by_id(room_id);
    bool dispose_after = false;

    if (room == NULL) {
        room = room_query_get_by_room_id(room_id);
        dispose_after = true;
    }

    if (room == NULL) {
        goto cleanup;
    }

    // Staff can bypass this
    if (!player_has_fuse(player, "fuse_enter_locked_rooms")) {

        // Doorbell checking
        if (room->room_data->accesstype == 1 &&
            !room_is_owner(room, player->details->id)) {
            int message_id = 131; // "BC" - tell user there's no answer

            if (list_size(room->users) > 0 && ring_doorbell_alerted(room, player)) {
                message_id = 91; // "A[" - tell user that you're waiting for doorbell
            }

            outgoing_message *om = om_create(message_id);
            player_send(player, om);
            om_cleanup(om);
            return;
        }

        // Password checking
        if (room->room_data->accesstype == 2 &&
            !room_is_owner(room, player->details->id)) {
            if (password == NULL || strcmp(password, room->room_data->password) != 0) {
                player_send_localised_error(player, "Incorrect flat password");
                return;
            }
        }
    }

    // Leave other room
    if (player->room_user->room != NULL) {
        room_leave(player->room_user->room, player, false);
    }

    player->room_user->authenticate_id = room_id;

    outgoing_message *interest = om_create(41); // "@i"
    player_send(player, interest);
    om_cleanup(interest);

    if (dispose_after) {
        room_dispose(room, false);
    }

    cleanup:
    free(content);
    free(password);
}

bool ring_doorbell_alerted(room *room, entity *ringing) {
    bool sent_ring_alert = false;

    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *room_player;
        list_get_at(room->users, i, (void *) &room_player);

        if (room_has_rights(room, room_player->details->id)) {
            outgoing_message *om = om_create(91); // "A["
            sb_add_string(om->sb, ringing->details->username);
            player_send(room_player, om);
            om_cleanup(om);

            sent_ring_alert = true;
        }
    }

    return sent_ring_alert;
}
