#include <stdbool.h>

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"

#include "game/room/room.h"
#include "game/room/room_user.h"

void USER_CANCEL_TYPING(entity *player, incoming_message *im) {
    if (player->room_user->room == NULL || !player->room_user->is_typing) {
        return;
    }

    player->room_user->is_typing = false;

    // TODO: don't forget to send this after a processed command like :ban
    // TODO: /\ This is what causes the weird speech bubble glitch on most Retro's
    // TODO: So let's fix that in the future

    outgoing_message *om = om_create(361); // "Ei"
    om_write_int(om, player->room_user->instance_id);
    om_write_int(om, 0);
    room_send(player->room_user->room, om);
    om_cleanup(om);
}
