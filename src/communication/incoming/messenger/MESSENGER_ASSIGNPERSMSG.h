#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/player_query.h"

void MESSENGER_ASSIGNPERSMSG(entity *player, incoming_message *message) {
    if (player->details == NULL) {
        return;
    }

    char *console_motto = im_read_str(message);

    if (console_motto == NULL) {
        return;
    }

    free(player->details->console_motto);
    player->details->console_motto = console_motto;

    outgoing_message *response = om_create(147); // "BS"
    om_write_str(response, player->details->console_motto);
    player_send(player, response);
    om_cleanup(response);

    player_query_save_motto(player);
}
