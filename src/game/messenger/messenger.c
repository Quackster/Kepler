#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "list.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"
#include "game/messenger/messenger.h"
#include "game/messenger/messenger_entry.h"

#include "database/queries/messenger_query.h"

/**
 * Create messenger instance.
 *
 * @return the messenger instance
 */
messenger *messenger_create() {
    messenger *messenger_manager = malloc(sizeof(messenger));
    messenger_manager->friends = NULL;
    messenger_manager->requests = NULL;
    messenger_manager->messages = NULL;
    return messenger_manager;
}

/**
 * Create the messenger message instance.
 *
 * @param id the id of the message
 * @param sender_id the sender user id
 * @param receiver_id the receiver user id
 * @param body the body of the message
 * @param date the date the message was sent
 * @return
 */
messenger_message *messenger_message_create(int id, int sender_id, int receiver_id, char *body, char *date) {
    messenger_message *message = malloc(sizeof(messenger_message));
    message->id = id;
    message->sender_id = sender_id;
    message->receiver_id = receiver_id;
    message->body = strdup(body);
    message->date = strdup(date);
    return message;
}

/**
 * Initialise the messenger for the player.
 *
 * @param player the player instance
 */
void messenger_init(entity *player) {
    if (player->messenger->friends == NULL) {
        player->messenger->friends = messenger_query_get_friends(player->details->id);
    }

    if (player->messenger->requests == NULL) {
        player->messenger->requests = messenger_query_get_requests(player->details->id);
    }

    if (player->messenger->messages == NULL) {
        player->messenger->messages = messenger_query_unread_messages(player->details->id);
    }
}

/**
 * Get whether the user has a friend with a certain user id.
 *
 * @param messenger the messenger instance
 * @param user_id the user id to check for
 * @return true, if successful
 */
int messenger_is_friends(messenger *messenger, int user_id) {
    for (size_t i = 0; i < list_size(messenger->friends); i++) {
        messenger_entry *friend;
        list_get_at(messenger->friends, i, (void*)&friend);

        if (friend->user_id == user_id) {
            return 1;
        }
    }

    return 0;
}

/**
 * Get whether the user has a request from a certain user id.
 *
 * @param messenger the messenger instance
 * @param user_id the user id to check for
 * @return true, if successful
 */
int messenger_has_request(messenger *messenger, int user_id) {
    for (size_t i = 0; i < list_size(messenger->requests); i++) {
        messenger_entry *friend;
        list_get_at(messenger->requests, i, (void*)&friend);

        if (friend->user_id == user_id) {
            return 1;
        }
    }

    return 0;
}

/**
 * Remove request from list
 *
 * @param messenger the messenger instance
 * @param user_id the user id
 */
void messenger_remove_request(messenger *messenger, int user_id) {
    for (size_t i = 0; i < list_size(messenger->requests); i++) {
        messenger_entry *friend;
        list_get_at(messenger->requests, i, (void*)&friend);

        if (friend->user_id == user_id) {
            list_remove_at(messenger->requests, i, NULL);
        }
    }
}

/**
 * Remove friend from list.
 *
 * @param messenger the messenger instance
 * @param user_id the user id
 */
void messenger_remove_friend(messenger *messenger, int user_id) {
    for (size_t i = 0; i < list_size(messenger->friends); i++) {
        messenger_entry *friend;
        list_get_at(messenger->friends, i, (void*)&friend);

        if (friend->user_id == user_id) {
            list_remove_at(messenger->friends, i, NULL);
        }
    }
}

/**
 * Method for cleaning up requests and friends list.
 *
 * @param messenger_entries the list of messenger entries
 */
void messenger_cleanup_list(List *messenger_entries) {
    for (size_t i = 0; i < list_size(messenger_entries); i++) {
        messenger_entry *entry;
        list_get_at(messenger_entries, i, (void*)&entry);
        messenger_entry_cleanup(entry);
    }

    list_destroy(messenger_entries);
}

/**
 * Clear requests, friends and messenger messages struct list.
 * 
 * @param messenger the messenger struct instance
 */
void messenger_dispose(messenger *messenger_manager) {
    if (messenger_manager->friends != NULL) {
        messenger_cleanup_list(messenger_manager->friends);
        messenger_manager->friends = NULL;
    }

    if (messenger_manager->requests != NULL) {
        messenger_cleanup_list(messenger_manager->requests);
        messenger_manager->requests = NULL;
    }

    if (messenger_manager->messages != NULL) {
        // Clear requests list
        for (size_t i = 0; i < list_size(messenger_manager->messages); i++) {
            messenger_message *msg;
            list_get_at(messenger_manager->messages, i, (void*)&msg);
            
            free(msg->body);
            free(msg->date);
            free(msg);
        }

        list_destroy(messenger_manager->messages);
        messenger_manager->messages = NULL;
    }

    free(messenger_manager);
}