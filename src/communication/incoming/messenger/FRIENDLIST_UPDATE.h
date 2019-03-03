#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void FRIENDLIST_UPDATE(entity *player, incoming_message *message) {
    outgoing_message *om = om_create(13); // "@M"
    om_write_int(om, (int) list_size(player->messenger->friends));

    for (size_t i = 0; i < list_size(player->messenger->friends); i++) {
        messenger_entry *entry;
        list_get_at(player->messenger->friends, i, (void *) &entry);

        entity_data *data = player_manager_get_data_by_id(entry->user_id);

        if (data == NULL) {
            continue;
        }

        om_write_int(om, entry->user_id);
        om_write_str(om, data->console_motto);

        entity *player_friend = player_manager_find_by_id(entry->user_id);
        om_write_int(om, (player_friend != NULL) ? true : false);

        if (player_friend != NULL) {
            if (player_friend->room_user->room != NULL) {

                bool is_public = (player_friend->room_user->room->room_data->owner_id == 0);

                if (!is_public) {
                    om_write_str(om, "Floor1a");
                } else {
                    om_write_str(om, player_friend->room_user->room->room_data->name);
                }
            } else {
                om_write_str(om, "On Hotel View");
            }
        } else {
            char *date = get_time_formatted_custom(data->last_online);
            om_write_str(om, date);
            free(date);
        }
    }

    player_send(player, om);
    om_cleanup(om);
}
