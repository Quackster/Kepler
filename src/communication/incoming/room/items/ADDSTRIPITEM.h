#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void ADDSTRIPITEM(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    if (!room_is_owner(player->room_user->room, player->details->id)) {
        return;
    }

    char *content = im_get_content(message);

    if (content == NULL) {
        return;
    }

    char *remove_data_item_id = get_argument(content, " ", 2);

    if (remove_data_item_id == NULL) {
        goto cleanup;
    }

    item *item = room_item_manager_get(player->room_user->room, (int)strtol(remove_data_item_id, NULL, 10));

    if (item == NULL || item->definition->behaviour->is_post_it) {
        goto cleanup;
    }

    room_map_remove_item(player->room_user->room, item);

    inventory *inv = (inventory *) player->inventory;
    list_add(inv->items, item);
    inventory_send(inv, "update", player);

    cleanup:
    free(content);
    free(remove_data_item_id);
}
