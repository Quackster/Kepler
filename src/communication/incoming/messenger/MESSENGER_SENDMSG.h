#include "list.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"
#include "database/queries/messenger_query.h"

void MESSENGER_SENDMSG(entity *player, incoming_message *message) {
    int friend_count = im_read_vl64(message);

    List *friends;
    list_new(&friends);

    for (int i = 0; i < friend_count; i++) {
        int friend_id = im_read_vl64(message);

        if (friend_id == -1) {
            continue;
        }

        list_add(friends, messenger_entry_create(friend_id));
    }

    char *chat_message = im_read_str(message);

    if (chat_message == NULL) {
        goto cleanup;
    }

    filter_vulnerable_characters(&chat_message, false);

    for (size_t i = 0; i < list_size(friends); i++) {
        messenger_entry *friend;
        list_get_at(friends, i, (void*)&friend);

        char *date = get_time_formatted();
        int message_id = messenger_query_new_message(friend->user_id, player->details->id, chat_message, date);

        entity *player_friend = player_manager_find_by_id(friend->user_id);

        if (player_friend != NULL) {
            outgoing_message *response = om_create(134); // "BF"
            //om_write_int(response, 1);
            om_write_int(response, message_id);
            om_write_int(response, player->details->id);
            om_write_str(response, date);
            om_write_str(response, chat_message);
            player_send(player_friend, response);
            om_cleanup(response);
        }

        messenger_entry_cleanup(friend);
        free(date);
    }

    cleanup:
    free(chat_message);
    list_destroy(friends);
}