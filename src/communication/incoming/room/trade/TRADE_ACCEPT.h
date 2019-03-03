#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void TRADE_ACCEPT(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }
    
    if (player->room_user->trade_partner == NULL) {
        return;
    }

    player->room_user->trade_accept = true;

    trade_manager_refresh_boxes(player->room_user);
    trade_manager_refresh_boxes(player->room_user->trade_partner);

    // Both agree, time to trade!
    if (player->room_user->trade_accept && player->room_user->trade_partner->trade_accept) {
        trade_manager_add_items(player->room_user, player->room_user->trade_partner);
        trade_manager_add_items(player->room_user->trade_partner, player->room_user);

        // Close all trading users for all users
        trade_manager_close(player->room_user);
    }
}
