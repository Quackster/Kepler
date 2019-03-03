#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/rooms/room_user_query.h"

void CREATEFLAT(entity *player, incoming_message *message) {
    // Client [0.0.0.0] incoming data: 29 / @]/first floor/xddd/model_a/open/1
    char *content = im_get_content(message);
    char *floor_setting = get_argument(content, "/", 0);
    char *room_name = get_argument(content, "/", 1);
    char *room_select_model = get_argument(content, "/", 2);
    char *room_setting = get_argument(content, "/", 3);
    char *room_show_name = get_argument(content, "/", 4);

    filter_vulnerable_characters(&room_name, true);
    filter_vulnerable_characters(&room_select_model, true);
    filter_vulnerable_characters(&room_setting, true);
    filter_vulnerable_characters(&room_show_name, true);

    if (strcmp(floor_setting, "first floor") != 0) {
        goto cleanup;
        return;
    }

    room_model *model = model_manager_get(room_select_model);

    if (model == NULL) {
        goto cleanup;
        return;
    }

    if (list_size(model->public_items) > 0) { // model is a public rooms model
        goto cleanup;
        return;
    }

    int room_id = room_query_create(player->details->id, room_name, room_select_model, room_show_name);
    room_manager_add(room_id);

    outgoing_message *om = om_create(59); // "@{"
    sb_add_int(om->sb, room_id);
    sb_add_char(om->sb, 13);
    sb_add_string(om->sb, room_name);
    player_send(player, om);
    om_cleanup(om);

    cleanup:
        free(floor_setting);
        free(room_name);
        free(room_select_model);
        free(room_setting);
        free(room_show_name);
        free(content);
}


