#include "list.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/messenger/messenger.h"
#include "game/player/player.h"

#include "database/queries/messenger_query.h"

void MESSENGER_REMOVEBUDDY(entity *player, incoming_message *message) {
    im_read(message, 1); // garbage data

    int friend_id = im_read_vl64(message);

    if (!messenger_is_friends(player->messenger, friend_id)) {
        return;
    }

    outgoing_message *response = om_create(138); // "BJ"
    om_write_int(response, 1);
    om_write_int(response, friend_id);
    player_send(player, response);
    om_cleanup(response);

    entity *friend = player_manager_find_by_id(friend_id);

    if (friend != NULL) {    
        response = om_create(138); // "BJ"
        om_write_int(response, 1);
        om_write_int(response, player->details->id);
        player_send(friend, response);
        om_cleanup(response);

        messenger_remove_friend(friend->messenger, friend_id);
    }

    messenger_remove_friend(player->messenger, friend_id);
    
    messenger_query_delete_friend(player->details->id, friend_id);
    messenger_query_delete_friend(friend_id, player->details->id);
}
