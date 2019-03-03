#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/inventory/inventory.h"

void MOVESTUFF(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    if (!room_has_rights(player->room_user->room, player->details->id)) {
        return;
    }

    char *content = im_get_content(message);

    if (content == NULL) {
        return;
    }

    char *str_id = get_argument(content, " ", 0);
    char *str_x = NULL;
    char *str_y = NULL;
    char *str_rot = NULL;

    if (str_id == NULL) {
        goto cleanup;
    }

    item *item = room_item_manager_get(player->room_user->room, (int)strtol(str_id, NULL, 10));

    if (item == NULL) {
        goto cleanup;
    }

    if (item->definition->behaviour->is_wall_item) {
        goto cleanup;
    }

    str_x = get_argument(content, " ", 1);
    str_y = get_argument(content, " ", 2);
    str_rot = get_argument(content, " ", 3);

    if (str_x == NULL || str_y == NULL || str_rot == NULL) {
        goto cleanup;
    }

    coord old_position;
    old_position.x = item->position->x;
    old_position.y = item->position->y;
    old_position.rotation = item->position->rotation;

    if (old_position.x == (int) strtol(str_x, NULL, 10) &&
        old_position.y == (int) strtol(str_y, NULL, 10) &&
        old_position.rotation == (int) strtol(str_rot, NULL, 10)) {

        // Send item update even though we cancelled, otherwise the client will be confused.
        char *item_str = item_as_string(item);

        outgoing_message *om = om_create(95); // "A_"
        sb_add_string(om->sb, item_str);
        room_send(player->room_user->room, om);
        om_cleanup(om);

        free(item_str);
        goto cleanup; // Do absolutely nothing because the item technically didn't move at all
    }

    item->position->x = (int) strtol(str_x, NULL, 10);
    item->position->y = (int) strtol(str_y, NULL, 10);
    item->position->rotation = (int) strtol(str_rot, NULL, 10);

    bool rotation = false;

    if (item->position->rotation != old_position.rotation) {
        rotation = true;
    }

    room_map_move_item(player->room_user->room, item, rotation, &old_position);

    cleanup:
        free(content);
		free(str_id);
		free(str_x);
		free(str_rot);
		free(str_y);
}
