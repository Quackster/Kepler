#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void SHOUT(entity *player, incoming_message *im) {
    if (player->room_user->room == NULL) {
        return;
    }

    char *message = im_read_str(im);

    if (message == NULL) {
        return;
    }

    filter_vulnerable_characters(&message, true);

    // Process command
    if (room_user_process_command(player->room_user, message)) {
        if (player->room_user->is_typing) {
            // Send cancel typing packet to room
            outgoing_message *om = om_create(361); // "Ei"
            om_write_int(om, player->room_user->instance_id);
            om_write_int(om, 0);
            room_send(player->room_user->room, om);
            om_cleanup(om);

            player->room_user->is_typing = false;
        }

        goto cleanup;
    }

    room_user_show_chat(player->room_user, message, true);
    room_user_reset_idle_timer(player->room_user);

    outgoing_message *om = om_create(26); // "@Z"
    om_write_int(om, player->room_user->instance_id);
    om_write_str(om, message);
    room_send(player->room_user->room, om);
    om_cleanup(om);

    cleanup:
    free(message);
}
