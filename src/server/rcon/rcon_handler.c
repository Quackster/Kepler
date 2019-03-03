#include "list.h"

#include "rcon_handler.h"
#include "rcon_listener.h"

#include "game/player/player_manager.h"
#include "game/player/player.h"
#include "game/player/player_refresh.h"

#include "shared.h"
#include "log.h"

void rcon_handle_command(uv_stream_t *handle, char header, char *message) {
    if (header == '1') { // "GET_USERS"
        char users_online[10];
        sprintf(users_online, "%i", (int) list_size(global.player_manager.players));

        rcon_send(handle, users_online);
    }

    if (header == '2') { // "REFRESH_APPEARANCE"
        int player_id = (int)strtol(message, NULL, 10);

        log_debug("RCON: refresh appearance for user id %u", player_id);

        entity *p = player_manager_find_by_id(player_id);

        if (p == NULL) {
            return;
        }

        player_refresh_appearance(p);
    }

    if (header == 'h') { // "HOTEL_ALERT"
        log_debug("RCON: Mass hotel alert message: %s", message);

        for (size_t i = 0; i < list_size(global.player_manager.players); i++) {
            entity *player;
            list_get_at(global.player_manager.players, i, (void *) &player);

            if (player->disconnected || !player->logged_in) {
                continue;
            }

            player_send_alert(player, message);
        }
    }
}

void rcon_send(uv_stream_t *handle, char *data) {
    uv_write_t *req;

    if(!(req = malloc(sizeof(uv_write_t)))){
        return;
    }

    size_t message_length = strlen(data);

    uv_buf_t buffer = uv_buf_init(malloc(message_length), (unsigned int) message_length);
    memcpy(buffer.base, data, message_length);

    req->handle = handle;
    req->data = buffer.base;

    int response = uv_write(req, handle, &buffer, 1, &rcon_on_write);
}
