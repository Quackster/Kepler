#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"

#include "game/room/room.h"

void WAVE(entity *player, incoming_message *im) {
    if (player->room_user->room == NULL) {
        return;
    }

    if (!room_user_has_status(player->room_user, "wave")) {
        room_user_add_status(player->room_user, "wave", "", 2, "", -1, -1);
        player->room_user->needs_update = true;
    }

    room_user_reset_idle_timer(player->room_user);
}
