#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void SIGN(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        goto cleanup;
    }

    char *vote = im_get_content(message);

    if (vote == NULL) {
        return;
    }

    room_user *room_entity = (room_user *) player->room_user;

    if (room_entity->room == NULL) {
        goto cleanup;
    }

    if (!is_numeric(vote)) {
        goto cleanup;
    }

    int voting_id = (int) strtol(vote, NULL, 10);

    if (voting_id < 0) {
        goto cleanup;
    }

    if (voting_id <= 7) { // Lido voting
        room_entity->lido_vote = (voting_id + 3);
    }

    char vote_id[11];
    sprintf(vote_id, " %i", voting_id);

    room_user_add_status(room_entity, "sign", vote_id, 5, "", -1, -1);
    room_user_reset_idle_timer(player->room_user);

    room_entity->needs_update = true;

    cleanup:
    free(vote);
}
