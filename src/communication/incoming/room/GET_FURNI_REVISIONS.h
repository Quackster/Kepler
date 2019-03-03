#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void GET_FURNI_REVISIONS(entity *player, incoming_message *message) {
    outgoing_message *om = om_create(295); // "Dg"
    om_write_int(om, 0);
    player_send(player, om);
    om_cleanup(om);
}
