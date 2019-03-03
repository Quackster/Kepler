#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void APPROVEEMAIL(entity *player, incoming_message *message) {
    outgoing_message *om = om_create(271); // "DO"
    player_send(player, om);
    om_cleanup(om);
}
