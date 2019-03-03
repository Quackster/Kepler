#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void G_STAT(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    room *room = player->room_user->room;

    if (room->room_data->model_data == NULL) {
        log_fatal("Room %i has invalid model data.", player->room_user->room->room_data->id);
        return;
    }

    outgoing_message *players = om_create(34); // "@b
    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *room_player;
        list_get_at(room->users, i, (void*)&room_player);

        append_user_status(players, room_player);
    }

    player_send(player, players);
    om_cleanup(players);

    room_refresh_rights(room, player);
    player->room_user->needs_update = true;
}
