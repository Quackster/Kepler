#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void TRADE_UNACCEPT(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }
    
    if (player->room_user->trade_partner == NULL) {
        return;
    }

    player->room_user->trade_accept = false;

    trade_manager_refresh_boxes(player->room_user);
    trade_manager_refresh_boxes(player->room_user->trade_partner);
}
