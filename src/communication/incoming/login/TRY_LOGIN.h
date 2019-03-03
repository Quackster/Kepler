#include "log.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player_refresh.h"
#include "database/queries/player_query.h"
#include "dispatch.h"

/*
 * Arguments we will send the to the login thread
 */
typedef struct login_context_s {
    char username[255];
    char password[255];
    entity *player;
} login_context;

/*
 * This function does the off-server-thread login
 */
void *do_login(void *args) {
    login_context *ctx = (login_context *)args;
    entity *player = ctx->player;

    int player_id = player_query_login(ctx->username, ctx->password);

    if (player_id == -1) {
        player_send_localised_error(player, "login incorrect");
    } else {
        entity_data *data = player_query_data(player_id);
        player->details = data;

        player_manager_destroy_session_by_id(player_id);
        player_login(player);
    }

    free(ctx);
}

/*
 * Off-server-thread login, as the password hashing function blocks the server thread for too long
 *
 * @param username Login username
 * @param password Login password
 */
void async_login(char *username, char *password, entity *player) {
    login_context *ctx = malloc(sizeof(login_context));
    strcpy(ctx->username, username);
    strcpy(ctx->password, password);
    ctx->player = player;

    hh_dispatch(StorageDispatch, (void*)do_login, (void*) ctx);
}

void TRY_LOGIN(entity *player, incoming_message *message) {
    char *username = im_read_str(message);
    char *password = im_read_str(message);

    if (username == NULL || password == NULL) {
        goto cleanup;
    }

    async_login(username, password, player);

    cleanup:
        free(username);
        free(password);
}