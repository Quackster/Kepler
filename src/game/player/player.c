#include <stdio.h>
#include <stdbool.h>

#include "uv.h"
#include "list.h"
#include "log.h"
#include "shared.h"

#include "game/inventory/inventory.h"
#include "game/messenger/messenger.h"

#include "game/player/player.h"
#include "game/player/player_manager.h"
#include "game/player/player_refresh.h"

#include "game/room/room.h"
#include "game/room/room_user.h"
#include "game/room/room_manager.h"
#include "game/room/manager/room_entity_manager.h"

#include "util/stringbuilder.h"
#include "util/configuration/configuration.h"

#include "communication/messages/outgoing_message.h"

#include "server/server_listener.h"
#include "database/queries/player_query.h"

/**
 * Creates a new player
 * @return player struct
 */
entity *player_create(void *socket, char *ip_address) {
    entity *player = malloc(sizeof(entity));
    player->entity_type = PLAYER_TYPE;
    player->stream = socket;
    player->disconnected = false;
    player->ip_address = strdup(ip_address);
    player->details = NULL;
    player->logged_in = false;
    player->ping_safe = true;
    player->room_user = NULL;
    player->messenger = NULL;
    player->inventory = NULL;
    player->last_stalk = time(0);
    return player;
}

/**
 * Creates a new player data instance
 * @return player data struct
 */
entity_data *player_create_data(int id, char *username, char *password, char *figure, char *pool_figure, int credits, char *motto, char *sex, int tickets, int film, int rank, char *console_motto, unsigned long last_online, unsigned long club_subscribed, unsigned long club_expiration, char *badge, bool badge_active) {
    entity_data *data = malloc(sizeof(entity_data));
    data->id = id;
    data->username = strdup(username);
    data->password = strdup(password);
    data->figure = strdup(figure);
    data->pool_figure = strdup(pool_figure);
    data->credits = credits;
    data->motto = strdup(motto);
    data->console_motto = strdup(console_motto);
    data->sex = strdup(sex);
    data->tickets = tickets;
    data->film = film;
    data->rank = rank;
    data->last_online = last_online;
    data->club_subscribed = (time_t)club_subscribed;
    data->club_expiration = (time_t)club_expiration;
    data->badge = strdup(badge);
    data->badge_active = badge_active;
    return data;
}

/**
 * Called when a connection is successfully opened
 *
 * @param p the player struct
 */
void player_login(entity *player) {
    outgoing_message *om;

    player->room_user = room_user_create(player);
    player->messenger = messenger_create();
    player->inventory = inventory_create();

    messenger_init(player);
    inventory_init(player);

    player_query_save_last_online(player);
    room_manager_add_by_user_id(player->details->id);

    om = om_create(2); // @B
    fuserights_append(player->details->rank, om);
    player_send(player, om);
    om_cleanup(om);

    om = om_create(3); // @C
    player_send(player, om);
    om_cleanup(om);

    if (configuration_get_bool("welcome.message.enabled")) {
        char *welcome_template = configuration_get_string("welcome.message.content");
        char *welcome_custom = replace(welcome_template, "%username%", player->details->username);

        player_send_alert(player, welcome_custom);
        free(welcome_custom);
    }

    player->ping_safe = true;
    player->logged_in = true;
}

/**
 * Get if player has a fuse right or not.
 *
 * @param player the player to check for
 * @param fuse_right the fuse right to check
 * @return true, if successful
 */
bool player_has_fuse(entity *player, char *fuse_right) {
    return fuserights_has_permission(player->details->rank, fuse_right);
}

/**
 * Disconnect user
 *
 * @param p the player struct
 */
void player_disconnect(entity *p) {
    if (p == NULL || p->disconnected) {
        return;
    }

    uv_close((uv_handle_t *) p->stream, server_on_connection_close);
}

/**
 * Send an outgoing message to the socket
 *
 * @param p the player struct
 * @param om the outgoing message
 */
void player_send(entity *p, outgoing_message *om) {
    if (om == NULL || p == NULL || p->disconnected) {
        return;
    }

    om_finalise(om);

    if (configuration_get_bool("debug")) {
        char *preview = replace_unreadable_characters(om->sb->data);
        log_debug("Client [%s] outgoing data: %i / %s", p->ip_address, om->header_id, preview);
        free(preview);
    }

    uv_write_t *req;

    if(!(req = malloc(sizeof(uv_write_t)))){
        return;
    }

    size_t message_length = strlen(om->sb->data);

    uv_buf_t buffer = uv_buf_init(malloc(message_length), (unsigned int) message_length);
    memcpy(buffer.base, om->sb->data, message_length);

    req->handle = (void*) p;
    req->data = buffer.base;

    int response = uv_write(req, (uv_stream_t *) p->stream, &buffer, 1, &server_on_write);
}

/**
 * Called when a connection is closed
 * @param player the player struct
 */
void player_cleanup(entity *player) {
    if (player == NULL) {
        return;
    }

    player_manager_remove(player);

    if (player->room_user != NULL) {
        if (player->room_user->room != NULL) {
            room_leave(player->room_user->room, player, false);
        }
    }

    if (player->details != NULL) {
        player_query_save_last_online(player);

        List *rooms = room_manager_get_by_user_id(player->details->id);

        for (size_t i = 0; i < list_size(rooms); i++) {
            room *room;
            list_get_at(rooms, i, (void*)&room);

            if (room != NULL) {
                room_dispose(room, false);
            }
        }

        list_destroy(rooms);
    }

    if (player->room_user != NULL) {
        room_user_reset(player->room_user, true);
        player->room_user = NULL;
    }

    if (player->messenger != NULL) {
        messenger_dispose(player->messenger);
        player->messenger = NULL;
    }

    if (player->inventory != NULL) {
        inventory_dispose(player->inventory);
        player->inventory = NULL;
    }

    if (player->details != NULL) {
        player_data_cleanup(player->details);
        player->details = NULL;
    }

    free(player->ip_address);
    free(player->stream);
    free(player);
}

void player_data_cleanup(entity_data *player_data) {
    free(player_data->username);
    free(player_data->password);
    free(player_data->figure);
    free(player_data->pool_figure);
    free(player_data->motto);
    free(player_data->console_motto);
    free(player_data->sex);
    free(player_data->badge);
    free(player_data);
}