#include "shared.h"

#include "list.h"
#include "player.h"

#include "database/queries/player_query.h"
#include "server/server_listener.h"
/**
 * Create a new list to store players
 */
void player_manager_init() {
    list_new(&global.player_manager.players);
}

/**
 * Creates a new player when given a new network stream to add. Will return a
 * old player if the stream was already added.
 *
 * @param stream the dyad stream
 * @return the player
 */
entity *player_manager_add(void *stream, char *ip) {
    entity *p = player_create(stream, ip);
    list_add(global.player_manager.players, p);
    return p;
}

/**
 * Removes a player by the given stream
 * @param stream the dyad stream
 */
void player_manager_remove(entity *p) {
    if (list_contains(global.player_manager.players, p)) {
        list_remove(global.player_manager.players, p, NULL);
    }
}

/**
 * Find a player by user id
 *
 * @param player_id the player id
 * @return the player, if sound, otherwise returns NULL
 */
entity *player_manager_find_by_id(int player_id) {
    if (list_size(global.player_manager.players) > 0) {
        for (size_t i = 0; i < list_size(global.player_manager.players); i++) {
            entity *p;
            list_get_at(global.player_manager.players, i, (void *) &p);

            if (!p->logged_in) {
                continue;
            }

            if (p->details->id == player_id) {
                return p;
            }
        }
    }

    return NULL;
}

/**
 * Find a player by user id
 *
 * @param player_id the player id
 * @return the player, if sound, otherwise returns NULL
 */
entity *player_manager_find_by_name(char *name) {
    if (list_size(global.player_manager.players) > 0) {
        for (size_t i = 0; i < list_size(global.player_manager.players); i++) {
            entity *p;
            list_get_at(global.player_manager.players, i, (void *) &p);

            if (!p->logged_in) {
                continue;
            }

            if (strcmp(p->details->username, name) == 0) {
                return p;
            }
        }
    }

    return NULL;
}

/**
 * Find a player by user id
 *
 * @param player_id the player id
 * @return the player, if sound, otherwise returns NULL
 */
entity_data *player_manager_get_data_by_id(int player_id) {
    if (list_size(global.player_manager.players) > 0) {
        for (size_t i = 0; i < list_size(global.player_manager.players); i++) {
            entity *p;
            list_get_at(global.player_manager.players, i, (void *) &p);

            if (!p->logged_in) {
                continue;
            }

            if (p->details->id == player_id) {
                return (entity_data *) p->details;
            }
        }
    }

    return player_query_data(player_id);
}

/**
* Destroy session by player id
*
* @param player_id the player id
*/
void player_manager_destroy_session_by_id(int player_id) {
    for (size_t i = 0; i < list_size(global.player_manager.players); i++) {
        entity *p;
        list_get_at(global.player_manager.players, i, (void*)&p);

        if (!p->logged_in || p->details->id != player_id) {
            continue;
        }

        uv_close((uv_handle_t *) p->stream, server_on_connection_close);
    }
}

/**
 * Dispose model manager
 */
void player_manager_dispose() {
    for (size_t i = 0; i < list_size(global.player_manager.players); i++) {
        entity *player;
        list_get_at(global.player_manager.players, i, (void *) &player);
        player_disconnect(player);
    }

    list_destroy(global.player_manager.players);
}