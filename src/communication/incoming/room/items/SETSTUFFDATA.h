#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void SETSTUFFDATA(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    // If item is a public furniture then we do this...
    if (strstr(message->data, "/") != NULL) {
        log_debug("Infobus fridge called..");
        return;
    }

    char *str_item_id = im_read_str(message);
    char *str_data = im_read_str(message);

    if (str_item_id == NULL || str_data == NULL) {
        goto cleanup;
    }

    int item_id = (int) strtol(str_item_id, NULL, 10);
    item *item = room_item_manager_get(player->room_user->room, item_id);

    if (item == NULL || !item_contains_custom_data(item->definition)) {
        goto cleanup;
    }

    if (item->definition->behaviour->requires_rights_for_interaction &&
        !room_has_rights(player->room_user->room, player->details->id)) {
        goto cleanup;
    }

    char *new_data = NULL;

    if (!item->definition->behaviour->is_door) {
        if (item->definition->behaviour->custom_data_true_false && (strcmp(str_data, "TRUE") == 0 || strcmp(str_data, "FALSE") == 0)) {
            new_data = strdup(str_data);
        }

        if (item->definition->behaviour->custom_data_numeric_on_off && (strcmp(str_data, "2") == 0 || strcmp(str_data, "1") == 0)) {
            new_data = strdup(str_data);
        }

        if (item->definition->behaviour->custom_data_on_off && (strcmp(str_data, "ON") == 0 || strcmp(str_data, "OFF") == 0)) {
            new_data = strdup(str_data);
        }

        if (item->definition->behaviour->custom_data_numeric_state) {
            if (str_data[0] != 'x') { // EXCEPTION: 00-99 for hockey light = 'x'
                if (is_numeric(str_data)) {
                    int state_id = (int)strtol(str_data, NULL, 10);

                    if (state_id >= 0 && state_id <= 99) {
                        char num[10];
                        sprintf(num, "%i", state_id);

                        new_data = strdup(num);
                    }
                }
            }
        }

    } else {
        if (strcmp(str_data, "O") == 0 || strcmp(str_data, "C") == 0) {
            new_data = strdup(str_data);
        }
    }

    if (new_data != NULL) {
        item_set_custom_data(item, new_data);
        item_broadcast_custom_data(item, new_data);

        if (!item->definition->behaviour->custom_data_true_false) {
            item_query_save(item);
        }
    }

    cleanup:
    free(str_item_id);
    free(str_data);
}