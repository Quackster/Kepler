#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/messenger_query.h"

void remove_friend(entity *player, int friend_id) {
    messenger_query_delete_request(player->details->id, friend_id);
    messenger_query_delete_request(friend_id, player->details->id);

    entity *friend = player_manager_find_by_id(friend_id);

    if (friend != NULL) {
        messenger_remove_request((messenger *) friend->messenger, friend_id);
    }
}

void MESSENGER_DECLINEBUDDY(entity *session, incoming_message *message) {
    int decline_option = im_read_vl64(message);
    
    if (decline_option > 0) {
        int request_id = im_read_vl64(message);
        remove_friend(session, request_id);
    } else {
        for (size_t i = 0; i < list_size(session->messenger->requests); i++) {
            messenger_entry *request;
            list_get_at(session->messenger->requests, i, (void*)&request);
            
            int request_id = request->user_id;
            remove_friend(session, request_id);
        }
    }
}

