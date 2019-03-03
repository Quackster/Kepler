#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

// Credits to Nillus from Woodpecker v3, very helpful code!
void GETSTRIP(entity *player, incoming_message *im) {
    char *strip_view = im_get_content(im);

    inventory *inv = (inventory *) player->inventory;
    inventory_send(inv, strip_view, player);

    if (strip_view != NULL) {
        free(strip_view);
    }
}
