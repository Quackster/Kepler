#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void TRADE_CLOSE(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    if (player->room_user->trade_partner == NULL) {
        return;
    }

    trade_manager_close(player->room_user);
}
