#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void G_ITEMS(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    List *wall_items = room_item_manager_wall_items(player->room_user->room);

    outgoing_message *om = om_create(45); // "@m"

    for (size_t i = 0; i < list_size(wall_items); i++) {
        item *room_item;
        list_get_at(wall_items, i, (void*)&room_item);
        char *item_string = item_as_string(room_item);

        sb_add_string(om->sb, item_string);
        sb_add_char(om->sb, 13);

        free(item_string);
    }

    player_send(player, om);
    om_cleanup(om);

    list_destroy(wall_items);
}
