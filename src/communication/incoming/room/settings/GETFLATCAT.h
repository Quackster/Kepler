#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void GETFLATCAT(entity *player, incoming_message *message) {
    int room_id = im_read_vl64(message);
    room *room = room_manager_get_by_id(room_id);

    if (room == NULL) {
        return;
    }

    outgoing_message *om = om_create(222); // "C^"
    om_write_int(om, room->room_id);
    om_write_int(om, room->room_data->category);
    player_send(player, om);
    om_cleanup(om);
}