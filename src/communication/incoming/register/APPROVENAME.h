#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void APPROVENAME(entity *player, incoming_message *message) {
    char *username = im_read_str(message);
    int name_check_code = get_name_check_code(username);

    if (username != NULL) {
        // Some fuckings to that guy that got rich of other people's work
        if (name_check_code == AARON_IS_A_FAG) {
            // TODO: get correct message ID for wrong name in register
            player_send_alert(player, "No faggots allowed");

            // Bye bye now!
            player_disconnect(player);

            goto cleanup;
        }

        outgoing_message *om = om_create(36); // "@d"
        om_write_int(om, name_check_code);
        player_send(player, om);
        om_cleanup(om);
    }

    cleanup:
        free(username);
}