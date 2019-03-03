#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/messenger_query.h"

#include "log.h"

void MESSENGER_REQUESTBUDDY(entity *player, incoming_message *message) {
    char *input_search = im_read_str(message);
    int search_id = player_query_id(input_search);

    if (search_id == -1) {
        goto cleanup;
    }

    if (messenger_is_friends(player->messenger, search_id)) {
        goto cleanup;
    }

    if (messenger_query_request_exists(player->details->id, search_id) ||
        messenger_query_request_exists(search_id, player->details->id)) {
        goto cleanup;
    }

    if (!messenger_query_new_request(player->details->id, search_id)) {
        goto cleanup;
    }

    entity *requested_player = player_manager_find_by_id(search_id);

    if (requested_player != NULL) {
        outgoing_message *response = om_create(132); // "BD"
        om_write_int(response, player->details->id);
        om_write_str(response, player->details->username);
        player_send(requested_player, response);
        om_cleanup(response);

        list_add(requested_player->messenger->requests, messenger_entry_create(player->details->id));
    }

    cleanup:
        free(input_search);
}
