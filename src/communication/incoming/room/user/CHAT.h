#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void CHAT(entity *player, incoming_message *im) {
    if (player->room_user->room == NULL) {
        return;
    }

    char *message = im_read_str(im);

    if (message != NULL) {
        filter_vulnerable_characters(&message, true);

        // Process command
        if (room_user_process_command(player->room_user, message)) {
            if (player->room_user->is_typing) {
                // Send cancel typing packet to room
                outgoing_message *om = om_create(361); // "Ei"
                om_write_int(om, player->room_user->instance_id);
                om_write_int(om, 0);
                room_send(player->room_user->room, om);
                om_cleanup(om);

                player->room_user->is_typing = false;
            }

            goto cleanup;
        }

        room_user_reset_idle_timer(player->room_user);
        room_user_show_chat(player->room_user, message, false);

        room *room = player->room_user->room;

        int source_x = player->room_user->position->x;
        int source_y = player->room_user->position->y;

        for (size_t i = 0; i < list_size(room->users); i++) {
            entity *room_player;
            list_get_at(room->users, i, (void *) &room_player);

            int dist_x = abs(source_x - room_player->room_user->position->x) - 1;
            int dist_y = abs(source_y - room_player->room_user->position->y) - 1;

            outgoing_message *om = om_create(24); // "@X"
            om_write_int(om, player->room_user->instance_id);

            if (dist_x < 9 && dist_y < 9) {// User can hear
                if (dist_x <= 6 && dist_y <= 6) {// User can hear full message
                    om_write_str(om, message);
                } else {
                    int garble_intensity = dist_x;

                    if (dist_y < dist_x) {
                        garble_intensity = dist_y;
                    }

                    garble_intensity -= 4;
                    char *garble_message = strdup(message);

                    for (int pos = 0; pos < strlen(garble_message); pos++) {
                        int intensity = ((rand() & 7) + garble_intensity);

                        if (intensity > 3 &&
                            garble_message[pos] != ' ' &&
                            garble_message[pos] != ',' &&
                            garble_message[pos] != '?' &&
                            garble_message[pos] != '!') {
                            garble_message[pos] = '.';
                        }
                    }

                    om_write_str(om, garble_message);
                    free(garble_message);
                }

                player_send(room_player, om);
                om_cleanup(om);
            }
        }
    }

    cleanup:
    free(message);
}
