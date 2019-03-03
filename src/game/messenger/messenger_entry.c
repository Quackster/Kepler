#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "list.h"

#include "communication/messages/outgoing_message.h"
#include "game/messenger/messenger_entry.h"

#include "game/player/player.h"
#include "game/player/player_manager.h"

#include "game/room/room.h"
#include "game/room/room_user.h"
#include "game/room/room_manager.h"

#include "game/room/mapping/room_model.h"

#include "shared.h"

/**
 * Create messenger entry instance
 * @param user_id the target user id
 * @return the messenger entry struct
 */
messenger_entry *messenger_entry_create(int user_id) {
    messenger_entry *entry = malloc(sizeof(messenger_entry));
    entry->user_id = user_id;
    return entry;
}

/**
 * Serialise messenger entries, used for both messenger search and messenger friends list.
 *
 * @param user_id the target user id to show
 * @param response the packet to append the data to
 */
void messenger_entry_serialise(int user_id, outgoing_message *response) {
    entity_data *data = player_manager_get_data_by_id(user_id);
    entity *search_player = player_manager_find_by_id(user_id);

    if (data != NULL) {
        om_write_int(response, data->id);
        om_write_str(response, data->username);
        om_write_int(response, strcmp(data->sex, "M") == 0);
        om_write_str(response, data->console_motto);

        int is_online = (search_player != NULL);
        om_write_int(response, is_online);

        if (is_online) {
            if (search_player->room_user->room != NULL) {
                room *room = room_manager_get_by_id(search_player->room_user->room_id);

                if (room->room_data->owner_id == 0) { // model is a public rooms model
                    om_write_str(response, room->room_data->name);
                } else {
                    om_write_str(response, "Floor1a");
                }
            } else {
                om_write_str(response, "On hotel view");
            }

        } else {
            om_write_str(response, "");
        }

        char *date = get_time_formatted_custom(data->last_online);
        om_write_str(response, date);
        om_write_str(response, data->figure);
        free(date);

        if (!is_online) {
            player_data_cleanup(data);
        }
    }
}

/**
 * Method for cleaning up the messenger entry instance.
 *
 * @param entry the messenger entry instance
 */
void messenger_entry_cleanup(messenger_entry *entry) {
    free(entry);
}