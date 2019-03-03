#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"

#include "game/room/room.h"
#include "game/room/room_user.h"

void WALK(entity *player, incoming_message *im) {
    if (player->room_user->room == NULL) {
        return;
    }

    if (player->room_user->walking_lock) {
        return;
    }

    int x = im_read_b64_int(im);
    int y = im_read_b64_int(im);
    
    walk_to(player->room_user, x, y);
    room_user_reset_idle_timer(player->room_user);
}
