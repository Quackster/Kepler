#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void CONVERT_FURNI_TO_CREDITS(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL || !room_is_owner(player->room_user->room, player->details->id)) {
        return;
    }

    int item_id = im_read_vl64(message);

    if (item_id < 0) {
        return;
    }

    item *item = room_item_manager_get(player->room_user->room, item_id);

    if (item == NULL || !item->definition->behaviour->is_redeemable) {
        return;
    }

    char* str_amount = get_argument(item->definition->sprite, "_", 1);

    if (!is_numeric(str_amount)) {
        goto cleanup;
    }

    int amount = (int) strtol(str_amount, NULL, 10);

    room_map_remove_item(player->room_user->room, item);

    item_query_delete(item_id);
    item_dispose(item);

    player->details->credits += amount;
    player_refresh_credits(player);

    player_query_save_currency(player);

    cleanup:
    free(str_amount);
}