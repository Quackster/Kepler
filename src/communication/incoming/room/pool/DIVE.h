#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/player_query.h"
#include "game/room/public_rooms/pool_handler.h"

void DIVE(entity *player, incoming_message *message) {
    char *diving_combination = im_get_content(message);

    if (diving_combination == NULL) {
        return;
    }

    if (player->room_user->room == NULL) {
        goto cleanup;
    }

    if (!player->room_user->is_diving) {
        goto cleanup;
    }

    // Send diving packet to everybody
    outgoing_message *refresh = om_create(74); // "AJ"
    sb_add_int(refresh->sb, player->room_user->instance_id);
    sb_add_char(refresh->sb, 13);
    sb_add_string(refresh->sb, diving_combination);
    room_send(player->room_user->room, refresh);
    om_cleanup(refresh);

    cleanup:
    free(diving_combination);
}
