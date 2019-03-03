#include <stdbool.h>

#include "log.h"
#include "array.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void update_badge(room_user *room_user) {
    outgoing_message *badge_notify = om_create(228); // "Cd"

    om_write_int(badge_notify, room_user->instance_id);

    if (room_user->entity->details->badge_active) {
        om_write_str(badge_notify, room_user->entity->details->badge);
    }

    room_send(room_user->room, badge_notify);
    om_cleanup(badge_notify);
}

void SETBADGE(entity *player, incoming_message *im) {
    char *new_badge = im_read_str(im);
    int show_badge = im_read_vl64(im);

    if (show_badge == 1) {
        // TODO: validate if user actually has badge
        Array *badges = player_query_badges(player->details->id);

        // Return if player doesn't own this badge
        if (array_contains_value(badges, new_badge, CC_CMP_STRING) == 0) {
            return;
        }
    }

    player->details->badge = strdup(new_badge);
    player->details->badge_active = (bool)show_badge;

    free(new_badge);

    update_badge(player->room_user);
    player_query_save_badge(player);
}
