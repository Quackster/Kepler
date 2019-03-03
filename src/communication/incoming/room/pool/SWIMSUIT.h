#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/player_query.h"
#include "game/room/public_rooms/pool_handler.h"

void SWIMSUIT(entity *player, incoming_message *message) {
    char *content = im_get_content(message);

    if (content == NULL) {
        return;
    }

    if (player->room_user->room == NULL) {
        goto cleanup;
    }

    // Update pool figure
    free(player->details->pool_figure);
    player->details->pool_figure = strdup(content);

    // Refresh pool figure
    outgoing_message *refresh = om_create(28); // "@\"
    append_user_list(refresh, player);
    room_send(player->room_user->room, refresh);
    om_cleanup(refresh);

    // Call handler to exit booth
    pool_booth_exit(player);

    // Save looks to database
    player_query_save_details(player);

    cleanup:
    free(content);
}
