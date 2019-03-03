#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void GET_SONG_LIST(entity *player, incoming_message *message) {
    outgoing_message *om;

    om = om_create(322); // "EB"
    om_write_int(om, 3);
    om_write_int(om, 1);
    om_write_int(om, 2);
    om_write_int(om, 3);
    player_send(player, om);
    om_cleanup(om);
}
