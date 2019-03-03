#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/items/item_query.h"

#include "game/items/item.h"
#include "game/inventory/inventory.h"

void do_purchase(entity *player, item_definition *def, char *extra_data, int special_sprite_id);

void GRPC(entity *player, incoming_message *message) {
    char *content = im_get_content(message);

    char *page = get_argument(content, "\r", 1);
    char *sale_code = get_argument(content, "\r", 3);


    if (page == NULL) {
        goto cleanup;
    }

    if (sale_code == NULL) {
        goto cleanup;
    }

    catalogue_page *store_page = catalogue_manager_get_page_by_index(page);

    if (store_page == NULL) {
        goto cleanup;
    }

    if (player->details->rank < store_page->min_role) {
        goto cleanup;
    }

    catalogue_item *store_item = catalogue_manager_get_item(sale_code);

    if (store_item == NULL) {
        goto cleanup;
    }

    if (store_item->price > player->details->credits) {
        outgoing_message *om = om_create(68); // "AD"
        player_send(player, om);
        om_cleanup(om);

        goto cleanup;
    }
    if (!store_item->is_package) {
        char *extra_data = get_argument(content, "\r", 4);
        do_purchase(player, store_item->definition, extra_data, store_item->item_specialspriteid);

        free(extra_data);
    } else {
        for (size_t i = 0; i < list_size(store_item->packages); i++) {
            catalogue_package *package;
            list_get_at(store_item->packages, i, (void *) &package);

            for (int j = 0; j < package->amount; j++) {
                do_purchase(player, package->definition, NULL, package->special_sprite_id);
            }
        }
    }

    player->details->credits -= store_item->price;
    player_refresh_credits(player);

    player_query_save_currency(player);

    inventory *inv = (inventory *) player->inventory;
    inventory_send(inv, "last", player);

    cleanup:
    free(content);
    free(page);
    free(sale_code);
}

void do_purchase(entity *player, item_definition *def, char *extra_data, int special_sprite_id) {
    char *custom_data = NULL;

    if (extra_data != NULL) {
        if (def->behaviour->is_decoration) {
            if (is_numeric(extra_data)) {
                custom_data = strdup(extra_data);
            }
        } else {
            if (special_sprite_id > 0) {
                char num[10];
                sprintf(num, "%i", special_sprite_id);
                custom_data = strdup(num);
            }
        }

        if (def->behaviour->is_post_it) {
            custom_data = strdup("20");
        }

        if (def->behaviour->is_prize_trophy) {
            filter_vulnerable_characters(&extra_data, true);

            char *short_date = get_short_time_formatted();

            stringbuilder *sb = sb_create();
            sb_add_string(sb, player->details->username);
            sb_add_char(sb, 9);
            sb_add_string(sb, short_date);
            sb_add_char(sb, 9);
            sb_add_string(sb, extra_data);

            custom_data = strdup(sb->data);

            sb_cleanup(sb);
            free(short_date);
        }
    }

    // False by default, stops the teleporters having that flash effect, etc.
    if (custom_data == NULL) {
        if (def->behaviour->custom_data_true_false) {
            custom_data = strdup("FALSE");
        }
    }

    int item_id = item_query_create(player->details->id, 0, def->id, 0, 0, 0, 0, custom_data);
    item *inventory_item = item_create(item_id, 0, player->details->id, def->id, 0, 0, 0, NULL, 0, custom_data);

    list_add(player->inventory->items, inventory_item);
}