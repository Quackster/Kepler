#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void STOP(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    char *content = im_get_content(message);

    if (content == NULL) {
        return;
    }

    if (strcmp(content, "CarryItem") == 0) {
        room_user_remove_status(player->room_user, "carryd");
        room_user_remove_status(player->room_user, "carryf");
    }

    if (strcmp(content, "Dance") == 0) {
        room_user_remove_status(player->room_user, "dance");
    }

    room_user_reset_idle_timer(player->room_user);
    player->room_user->needs_update = true;

    free(content);
}