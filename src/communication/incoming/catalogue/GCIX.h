#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "hashtable.h"

#include "game/catalogue/catalogue_manager.h"
#include "game/catalogue/catalogue_page.h"

#include "game/player/player.h"

void GCIX(entity *player, incoming_message *message) {
    List *pages = catalogue_manager_get_pages();

    outgoing_message *catalogue_pages = om_create(126); // "A~"

    for (size_t i = 0; i < list_size(pages); i++) {
        catalogue_page *page = NULL;
        list_get_at(pages, i, (void *) &page);

        if (player->details->rank >= page->min_role) {
            om_write_str_delimeter(catalogue_pages, page->name_index, 9);
            om_write_str_delimeter(catalogue_pages, page->name, 13);
        }
    }

    player_send(player, catalogue_pages);
    om_cleanup(catalogue_pages);
}
