#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/catalogue/catalogue_manager.h"
#include "game/catalogue/catalogue_page.h"
#include "game/catalogue/catalogue_item.h"
#include "game/catalogue/catalogue_package.h"

void serialise_catalogue_item(catalogue_item *item, outgoing_message *message);

void GCAP(entity *player, incoming_message *message) {
    char *content = im_get_content(message);
    char *page_name = get_argument(content, "/", 1);

    catalogue_page *page = catalogue_manager_get_page_by_index(page_name);

    if (page == NULL) {
        goto cleanup;
    }

    outgoing_message *om = om_create(127); // "A"
    om_write_str_kv(om, "i", page->name_index);
    om_write_str_kv(om, "n", page->name);
    om_write_str_kv(om, "l", page->layout);
    om_write_str_kv(om, "g", page->image_headline);
    om_write_str_kv(om, "e", page->image_teasers);
    om_write_str_kv(om, "h", page->body);

    if (page->label_pick != NULL) {
        om_write_str_kv(om, "w", page->label_pick);
    }

    if (page->label_extra_s != NULL) {
        om_write_str_kv(om, "s", page->label_extra_s);
    }

    for (int attribute_id = 0; attribute_id < 12; attribute_id++) {
        char key[25], message_key[12];
        sprintf(key, "label_extra_t_%i", attribute_id);
        sprintf(message_key, "t%i", attribute_id);

        if (hashtable_contains_key(page->label_extra, key)) {
            char *value;
            hashtable_get(page->label_extra, key, (void*)&value);
            om_write_str_kv(om, message_key, value);
        }
    }

    for (size_t i = 0; i < list_size(page->items); i++) {
        catalogue_item * item = NULL;
        list_get_at(page->items, i, (void *) &item);
        serialise_catalogue_item(item, om);
    }

    player_send(player, om);
    om_cleanup(om);

    cleanup:
        free(content);
        free(page_name);
}

/**
 * Serialise the catalogue item for the page.
 *
 * @param item the catalogue item
 * @param message the catalogue page outgoing message
 */
void serialise_catalogue_item(catalogue_item *item, outgoing_message *message) {
    if (item->definition == NULL) {
        return;
    }

    char *item_name = catalogue_item_get_name(item);
    char *item_desc = catalogue_item_get_description(item);
    char *item_type = catalogue_item_get_type(item);
    char *item_icon = catalogue_item_get_icon(item);
    char *item_size = catalogue_item_get_size(item);
    char *item_dimensions = catalogue_item_get_dimensions(item);

    sb_add_string(message->sb, "p");
    sb_add_string(message->sb, ":");
    om_write_str_delimeter(message, item_name, 9);
    om_write_str_delimeter(message, item_desc, 9);
    sb_add_int(message->sb, item->price); sb_add_char(message->sb, 9);
    om_write_str_delimeter(message, "", 9);
    om_write_str_delimeter(message, item_type, 9);
    om_write_str_delimeter(message, item_icon, 9);
    om_write_str_delimeter(message, item_size, 9);
    om_write_str_delimeter(message, item_dimensions, 9);
    om_write_str_delimeter(message, item->sale_code, 9);

    if (item->is_package || strcmp(item->definition->sprite, "poster") == 0) {
        om_write_str_delimeter(message, "", 9);
    }

    if (item->is_package) {
        sb_add_int(message->sb, (int) list_size(item->packages)); sb_add_char(message->sb, 9);

        for (size_t i = 0; i < list_size(item->packages); i++) {
            catalogue_package *pkg = NULL; list_get_at(item->packages, i, (void *) &pkg);

            char *package_icon = item_definition_get_icon(pkg->definition, pkg->special_sprite_id);

            om_write_str_delimeter(message, package_icon, 9);
            sb_add_int(message->sb, pkg->amount); sb_add_char(message->sb, 9);
            om_write_str_delimeter(message, pkg->definition->colour, 9);

            free(package_icon);
        }

    } else {
        if (!item->definition->behaviour->is_wall_item) {
            om_write_str_delimeter(message, item->definition->colour, 9);
        }
    }

    sb_add_char(message->sb, 13);

    free(item_name);
    free(item_desc);
    free(item_type);
    free(item_icon);
    free(item_size);
    free(item_dimensions);
}
