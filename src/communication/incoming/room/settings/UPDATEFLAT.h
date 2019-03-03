#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/rooms/room_query.h"

void UPDATEFLAT(entity *player, incoming_message *message) {
    char *content = im_get_content(message);
    char *str_id = get_argument(content, "/", 0);

    char *str_name = get_argument(content, "/", 1);
    char *str_access_type = get_argument(content, "/", 2);
    char *str_show_owner = get_argument(content, "/", 3);

    room *room = room_manager_get_by_id((int)strtol(str_id, NULL, 10));

    if (room == NULL) {
        goto cleanup;
    }

    if (!room_is_owner(room, player->details->id)) {
        goto cleanup;
    }

    room->room_data->name = strdup(str_name);

    if (strcmp(str_access_type, "open") == 0) {
        room->room_data->accesstype = 0;
    } else if (strcmp(str_access_type, "closed") == 0) {
        room->room_data->accesstype = 1;
    } else if (strcmp(str_access_type, "password") == 0) {
        room->room_data->accesstype = 2;
    }

    room->room_data->show_name = (strcmp(str_show_owner, "1") == 0);
    filter_vulnerable_characters(&room->room_data->name, true);

    query_room_save(room);

    cleanup:
        free(str_id);
        free(content);
        free(str_name);
        free(str_access_type);
        free(str_show_owner);
}