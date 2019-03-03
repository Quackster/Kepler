#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "list.h"

#include "game/items/item.h"
#include "game/player/player.h"

#include "game/room/room.h"
#include "game/room/manager/room_item_manager.h"

#include "util/stringbuilder.h"

void G_OBJS(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    room *room = player->room_user->room;

    List *public_items = room_item_manager_public_items(room);
    List *floor_items = room_item_manager_floor_items(room);

    outgoing_message *om = om_create(30); // "@^
    for (size_t i = 0; i < list_size(public_items); i++) {
        item *room_item;
        list_get_at(public_items, i, (void*)&room_item);

        char *item_string = item_as_string(room_item);
        sb_add_string(om->sb, item_string);
        free(item_string);
    }

    player_send(player, om);
    om_cleanup(om);

    om = om_create(32); // "@`"
    om_write_int(om, (int)list_size(floor_items));

    for (size_t i = 0; i < list_size(floor_items); i++) {
        item *item;
        list_get_at(floor_items, i, (void*)&item);

        char *item_string = item_as_string(item);
        sb_add_string(om->sb, item_string);
        free(item_string);
    }

    player_send(player, om);
    om_cleanup(om);

    list_destroy(public_items);
    list_destroy(floor_items);
}
