#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void PARENT_EMAIL_REQUIRED(entity *player, incoming_message *message) {
    outgoing_message *om = om_create(217); // "CY"
    om_write_str_int(om, 1);
    player_send(player, om);
    om_cleanup(om);
}