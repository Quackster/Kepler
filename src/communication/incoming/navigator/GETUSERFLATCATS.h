#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void GETUSERFLATCATS(entity *player, incoming_message *message) {
    outgoing_message *navigator = om_create(221); // "C]"

    // Count categories that this user can actually access
    List *categories = category_manager_flat_categories();
    int accessible_categories = 0;

    for (size_t i = 0; i < list_size(categories); i++) {
        room_category *category;
        list_get_at(categories, i, (void*)&category);

        if (player->details->rank >= category->minrole_setflatcat) {
            accessible_categories++;
        }
    }

    // Start appending accessible categories
    om_write_int(navigator, accessible_categories); // category count

    for (size_t i = 0; i < list_size(categories); i++) {
        room_category *category;
        list_get_at(categories, i, (void*)&category);

        if (player->details->rank >= category->minrole_setflatcat) {
            om_write_int(navigator, category->id); // category id
            om_write_str(navigator, category->name); // category name
        }
    }

    player_send(player, navigator);
    om_cleanup(navigator);
    
    list_destroy(categories);
}
