#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void TRADE_ADDITEM(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    if (player->room_user->trade_partner == NULL) {
        return;
    }

    char *content = im_get_content(message);

    if (content == NULL || !is_numeric(content)) {
        goto cleanup;
    }

    int item_id = (int)strtol(content, NULL, 10);

    player->room_user->trade_items[player->room_user->trade_item_count++] = item_id;

    trade_manager_refresh_boxes(player->room_user);
    trade_manager_refresh_boxes(player->room_user->trade_partner);

    cleanup:
    free(content);
}
