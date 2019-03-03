#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/player_query.h"

#include "game/pathfinder/coord.h"
#include "game/room/public_rooms/pool_handler.h"

#include "game/room/room_user.h"

#include "game/room/mapping/room_tile.h"
#include "game/room/mapping/room_map.h"

void SPLASHPOSITION(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        goto cleanup;
    }

    if (!player->room_user->is_diving) {
        return;
    }

    char *content = im_get_content(message);

    if (content == NULL) {
        return;
    }

    if (strstr(content, ",") == NULL) {
        goto cleanup;
    }

    char *content_x = get_argument(content, ",", 0);
    char *content_y = get_argument(content, ",", 1);

    if (!is_numeric(content_x) || !is_numeric(content_y)) {
        free(content_x);
        free(content_y);
        goto cleanup;
    }

    coord walk_destination = {};
    walk_destination.x = (int) strtol(content_x, NULL, 10);
    walk_destination.y = (int) strtol(content_y, NULL, 10);

    room_user *room_entity = player->room_user;
    room_user_reset_idle_timer(room_entity);

    room_tile *tile = room_entity->room->room_map->map[room_entity->position->x][room_entity->position->y];

    room_entity->position->x = walk_destination.x;
    room_entity->position->y = walk_destination.y;
    room_entity->position->z = room_entity->room->room_data->model_data->heights[room_entity->position->x][room_entity->position->y];
    room_entity->walking_lock = false;
    room_entity->is_diving = false;

    // Immediately update status
    room_user_add_status(room_entity, "swim", "", -1, "", -1, -1);

    outgoing_message *players = om_create(34); // "@b
    append_user_status(players, player);
    room_send(player->room_user->room, players);
    om_cleanup(players);

    // Walk to ladder exit
    walk_to(room_entity, 20, 19);

    // Open up lift
    if (tile != NULL && tile->highest_item != NULL) {
        item *item = tile->highest_item;

        if (strcmp(item->definition->sprite, "poolLift") == 0) {
            item_assign_program(item, "open");
        }
    }

    int total = 0, sum = 0;
    double final = 0;

    // Count votes
    for (size_t i = 0; i < list_size(room_entity->room->users); i++) {
        entity * room_player;
        list_get_at(room_entity->room->users, i, (void *) &room_player);

        if (room_player->details->id == player->details->id) {
            continue;
        }

        if (room_player->room_user->lido_vote > 0) {
            sum += room_player->room_user->lido_vote;
            total++;
        }
    }

    char target[200];
    sprintf(target, "targetcamera %i", player->room_user->instance_id);

    outgoing_message *target_diver = om_create(71); // "AG"
    sb_add_string(target_diver->sb, "cam1");
    sb_add_string(target_diver->sb, " ");
    sb_add_string(target_diver->sb, target);
    room_send((room *) room_entity->room, target_diver);
    om_cleanup(target_diver);

    // Show diving score
    if (total > 0) {
        final = (double) sum / total;

        char score_text[200];
        sprintf(score_text, "showtext %s's score:/%.1f", player->details->username, final);

        outgoing_message *score_message = om_create(71); // "AG"
        sb_add_string(score_message->sb, "cam1");
        sb_add_string(score_message->sb, " ");
        sb_add_string(score_message->sb, score_text);
        room_send((room *) room_entity->room, score_message);
        om_cleanup(score_message);
    }

    // Reset all diving scores
    for (size_t i = 0; i < list_size(room_entity->room->users); i++) {
        entity *room_player;
        list_get_at(room_entity->room->users, i, (void *) &room_player);

        if (room_player->room_user->lido_vote > 0) {
            room_player->room_user->lido_vote = -1;
        }
    }

    free(content_x);
    free(content_y);

    cleanup:
    free(content);
}
