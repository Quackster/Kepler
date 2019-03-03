#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"
#include "game/room/room_user.h"

#include "game/pathfinder/rotation.h"
#include "game/pathfinder/coord.h"

void LOOKTO(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    char *content = NULL;
    char *str_x = NULL;
    char *str_y = NULL;

    room_user *room_entity = player->room_user;

    if (room_user_has_status(room_entity, "sit") || room_user_has_status(room_entity, "lay")) {
        goto cleanup;
    }

    content = im_get_content(message);
    str_x = get_argument(content, " ", 0);
    str_y = get_argument(content, " ", 1);

    if (str_x == NULL || str_y == NULL || content == NULL) {
        goto cleanup;
    }

    int towards_x = (int) strtol(str_x, NULL, 10);
    int towards_y = (int) strtol(str_y, NULL, 10);

    if (towards_x == room_entity->position->x && towards_y == room_entity->position->y) {
        goto cleanup; // Don't process LOOKTO on your own coordinate
    }

    int rotation = calculate_human_direction(room_entity->position->x, room_entity->position->y, towards_x, towards_y);
    coord_set_rotation(room_entity->position, rotation, rotation);

    room_entity->needs_update = true;
    room_user_reset_idle_timer(player->room_user);

    cleanup:
    free(content);
    free(str_x);
    free(str_y);
        
}
