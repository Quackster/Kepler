#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/rooms/room_favourites_query.h"

void ADD_FAVOURITE_ROOM(entity *player, incoming_message *message) {
    im_read_vl64(message);

    int room_id = im_read_vl64(message);

    if (room_query_check_favourite(room_id, player->details->id) != -1) {
        return;
    }

    room_query_favourite(room_id, player->details->id);

}