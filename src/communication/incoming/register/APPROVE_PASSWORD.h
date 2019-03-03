#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void APPROVE_PASSWORD(entity *player, incoming_message *message) {
    char *username = im_read_str(message);
    char *password = im_read_str(message);

    outgoing_message *om = om_create(282); // "DZ"
    om_write_int(om, valid_password(username, password));
    player_send(player, om);
    om_cleanup(om);

    free(username);
    free(password);
}
