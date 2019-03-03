#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "hashtable.h"

#include "game/catalogue/catalogue_manager.h"
#include "game/catalogue/catalogue_page.h"
#include "game/catalogue/catalogue_item.h"

#include "game/player/player.h"

void GET_ALIAS_LIST(entity *player, incoming_message *message) {
    player_send(player, global.item_manager.sprite_index);

    outgoing_message *om = om_create(297); // "Di"
    om_write_int(om, 0);
    player_send(player, om);
    om_cleanup(om);
}