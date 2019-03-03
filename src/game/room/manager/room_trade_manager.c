#include <shared.h>
#include "game/player/player.h"

#include "list.h"

#include "game/items/item.h"
#include "game/room/room.h"
#include "game/room/room_user.h"

#include "game/room/manager/room_trade_manager.h"
#include "game/inventory/inventory.h"

#include "database/queries/items/item_query.h"

#include "communication/messages/outgoing_message.h"
#include "util/stringbuilder.h"

void trade_manager_reset(room_user *room_user);

/**
 * Close trade manager, automatically called when player leaves or joins a different room, disconnects, or
 * closes the trade window. Needs to be only called once, because its trade
 * partner will be handled in this function.
 *
 * @param room_user the room user trading
 */
void trade_manager_close(room_user *room_user) {
    if (room_user->trade_partner != NULL) {
        outgoing_message *om = om_create(110);

        player_send(room_user->entity, om);
        player_send(room_user->trade_partner->entity, om);

        om_cleanup(om);

        inventory_send(room_user->entity->inventory, "update", room_user->entity);
        inventory_send(room_user->trade_partner->entity->inventory, "update", room_user->trade_partner->entity);

        // Reset trade partner
        trade_manager_reset(room_user->trade_partner);
    }

    trade_manager_reset(room_user);
}

/**
 * Reset the trade partner back to default.
 *
 * @param room_user
 */
void trade_manager_reset(room_user *room_user) {
    memset(room_user->trade_items, -1, sizeof(room_user->trade_items));

    room_user->trade_item_count = 0;
    room_user->trade_partner = NULL;
    room_user->trade_accept = false;

    room_user_remove_status(room_user, "trd");
    room_user->needs_update = true;
}

/**
 * Refresh trade boxes, used when trade first starts, and to add items.
 *
 * @param room_user the room user to refrsh the trade boxes for
 */
void trade_manager_refresh_boxes(room_user *room_user) {
    if (room_user->trade_partner == NULL) {
        return;
    }

    outgoing_message *om = om_create(108); // "Al"
    om_write_str_delimeter(om, room_user->entity->details->username, 9);
    om_write_str_delimeter(om, room_user->trade_accept ? "true" : "false", 9);

    if (room_user->trade_item_count > 0) {
        for (int i = 0; i < room_user->trade_item_count; ++i) {
            item *item =  inventory_get_item(room_user->entity->inventory, room_user->trade_items[i]);

            char *strip_string = item_strip_string(item, i);
            sb_add_string(om->sb, strip_string);
            free(strip_string);
        }
    }

    om_write_char(om, 13);

    om_write_str_delimeter(om, room_user->trade_partner->entity->details->username, 9);
    om_write_str_delimeter(om, room_user->trade_partner->trade_accept ? "true" : "false", 9);

    if (room_user->trade_partner->trade_item_count > 0) {
        for (int i = 0; i < room_user->trade_partner->trade_item_count; ++i) {
            item *item =  inventory_get_item(room_user->trade_partner->entity->inventory, room_user->trade_partner->trade_items[i]);

            char *strip_string = item_strip_string(item, i);
            sb_add_string(om->sb, strip_string);
            free(strip_string);
        }
    }

    player_send(room_user->entity, om);
    om_cleanup(om);
}

void trade_manager_add_items(room_user *room_entity, room_user *trade_partner) {
    if (trade_partner == NULL) {
        return;
    }

    if (trade_partner->trade_item_count > 0) {
        for (int i = 0; i < trade_partner->trade_item_count; ++i) {
            item *item =  inventory_get_item(trade_partner->entity->inventory, trade_partner->trade_items[i]);
            item->owner_id = room_entity->entity->details->id;

            list_remove(trade_partner->entity->inventory->items, item, NULL);
            list_add(room_entity->entity->inventory->items, item);

            item_query_save(item);
        }
    }
}