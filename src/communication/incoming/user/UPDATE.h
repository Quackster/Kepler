#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void UPDATE(entity *player, incoming_message *message) {
    for (int i = 0; i < 3; i++) {
        int update_id = im_read_b64_int(message);

        if (update_id == 4) {
            char *content = im_read_str(message);
            filter_vulnerable_characters(&content, true);

            if (content != NULL) {
                if (is_numeric(content) && strlen(content) == 25) {
                    free(player->details->figure);
                    player->details->figure = content;
                } else {
                    free(content);
                }
            }
        }

        if (update_id == 5) {
            char *content = im_read_str(message);

            if (content != NULL) {
                if (player->details->sex[0] != content[0]) { // Changed sex? Reset pool figure
                    free(player->details->pool_figure);
                    player->details->pool_figure = strdup("");
                }

                if ((content[0] == 'M' || content[0] == 'F') && strlen(content) == 1) {
                    free(player->details->sex);
                    player->details->sex = content;
                } else {
                    free(content);
                }
            }
        }

        if (update_id == 6) {
            char *content = im_read_str(message);

            if (content != NULL) {
                free(player->details->motto);
                player->details->motto = content;
            }
        }
    }

    player_query_save_details(player);
    player_query_save_motto(player);
    GET_INFO(player, message);

    /*char *content;

    im_read_b64(message);
    content = im_read_str(message);

    if (content != NULL) {
        if (is_numeric(content)) {
            free(entity->details->figure);
            entity->details->figure = strdup(content);
            log_debug("figure: %s", entity->details->figure);
        }

        free(content);
    }

    im_read_b64(message);
    content = im_read_str(message);

    if (content != NULL) {
        if ((content[0] == 'M' || content[0] == 'F') && strlen(content) == 1) {
            char *sex = strdup(content);

            free(entity->details->sex);
            entity->details->sex = sex;

            log_debug("sex updated");
        }

        free(content);
    }

    im_read_b64(message);
    content = im_read_str(message);

    if (content != NULL) {
        free(entity->details->motto);
        entity->details->motto = strdup(content);
        log_debug("motto: %s", entity->details->motto);
        free(content);
    }*/
}
