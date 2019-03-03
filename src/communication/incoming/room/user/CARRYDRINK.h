#include "shared.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void CARRYDRINK(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    char *content = im_get_content(message);

    if (content == NULL) {
        return;
    }

    if (is_numeric(content)) {
        int drink_id = (int) strtol(content, NULL, 10);

        room_user_carry_item(player->room_user, drink_id, NULL);
    } else {
        room_user_carry_item(player->room_user, 0, content);
    }

    room_user_reset_idle_timer(player->room_user);
    free(content);
}