#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"
#include "game/room/room.h"

void QUIT(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    room_leave(player->room_user->room, player, false);
}
