#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void GETAVAILABLEBADGES(entity *player, incoming_message *im) {
    player_refresh_badges(player);
}
