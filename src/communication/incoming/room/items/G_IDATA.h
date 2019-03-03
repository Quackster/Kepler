#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void G_IDATA(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    char *str_item_id = im_get_content(message);

    if (str_item_id == NULL || !is_numeric(str_item_id)) {
        goto cleanup;
    }

    item *item = room_item_manager_get(player->room_user->room, (int) strtol(str_item_id, NULL, 10));

    if (item == NULL || !item->definition->behaviour->is_post_it) {
        goto cleanup;
    }

    char color[7];
    memcpy(color, &item->custom_data[0], 6);
    color[6] = '\0';

    outgoing_message *om = om_create(48); // "@p"
    om_write_str_delimeter(om, str_item_id, 9);
    sb_add_string(om->sb, color);
    sb_add_string(om->sb, " ");

    if (strlen(item->custom_data) > 6) {
        char *custom_data = (item->custom_data + 6);
        sb_add_string(om->sb, custom_data);
    }

    player_send(player, om);
    om_cleanup(om);

    cleanup:
    free(str_item_id);
}