#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void DANCE(entity *player, incoming_message *im) {
    if (player->room_user->room == NULL) {
        return;
    }

    if (room_user_has_status(player->room_user, "sit") || room_user_has_status(player->room_user, "lay")) {
        return;
    }

    char *content = im_get_content(im);
    int dance_id = im_read_vl64(im);

    if (content == NULL || strlen(content) == 0) {
        room_user_add_status(player->room_user, "dance", "", -1, "", -1, -1);
    } else {
        if (dance_id < 0 || dance_id > 4) {
            return;
        }

        if (!player_has_fuse(player, "fuse_use_club_dance")) {
            return;
        }

        char dance_value[11];
        sprintf(dance_value, " %i", dance_id);

        room_user_add_status(player->room_user, "dance", dance_value, -1, "", -1, -1);
    }

    room_user_remove_status(player->room_user, "carryd");
    room_user_remove_status(player->room_user, "carryf");

    room_user_reset_idle_timer(player->room_user);
    player->room_user->needs_update = true;

    free(content);
}
