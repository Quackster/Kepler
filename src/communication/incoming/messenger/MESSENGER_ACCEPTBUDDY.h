#include "list.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/messenger_query.h"

void MESSENGER_ACCEPTBUDDY(entity *player, incoming_message *message) {
    int amount = im_read_vl64(message);
    for (int i = 0; i < amount; i++) {
        int friend_id = im_read_vl64(message);

        if (!messenger_has_request(player->messenger, friend_id)) {
            return;
        }

        messenger_query_new_friend(friend_id, player->details->id);
        messenger_query_delete_request(player->details->id, friend_id);
        messenger_query_delete_request(friend_id, player->details->id);

        outgoing_message *response = om_create(137); // "BI"
        messenger_entry_serialise(friend_id, response);
        player_send(player, response);
        om_cleanup(response);

        entity *friend = player_manager_find_by_id(friend_id);

        if (friend != NULL) {
            response = om_create(137); // "BI"
            messenger_entry_serialise(player->details->id, response);
            player_send(friend, response);
            om_cleanup(response);

            list_add(friend->messenger->friends, messenger_entry_create(player->details->id));
        }

        list_add(player->messenger->friends, messenger_entry_create(friend_id));

        if (friend != NULL) {
            messenger_remove_request(friend->messenger, player->details->id);
        }
    }
}
