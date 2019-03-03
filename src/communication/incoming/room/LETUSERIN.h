#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void LETUSERIN(entity *user, incoming_message *message) {
    char *content = im_get_content(message);
    char *ringing_username = NULL;

    if (content == NULL) {
        goto cleanup;
    }

    if (user->room_user->room == NULL) {
        goto cleanup;
    }

    room *room = user->room_user->room;

    if (!room_has_rights(room, user->details->id)) {
        goto cleanup;
    }

    ringing_username = im_read_str(message);
    bool can_enter = content[strlen(content) - 1] == 'A';

    entity *ringer = player_manager_find_by_name(ringing_username);

    if (ringer == NULL) {
        goto cleanup;
    }

    int message_id;

    if (can_enter) {
        message_id = 41; // "@i"
        ringer->room_user->authenticate_id = room->room_id;
    } else {
        message_id = 131; // "BC"
    }

    outgoing_message *om = om_create(message_id);
    player_send(ringer, om);
    om_cleanup(om);

    cleanup:
    free(content);
    free(ringing_username);
}
