#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void PONG(entity *player, incoming_message *message) {
    player->ping_safe = true;
}