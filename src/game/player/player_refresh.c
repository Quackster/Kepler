#include "shared.h"

#include "list.h"
#include "log.h"

#include "player.h"
#include "player_refresh.h"

#include "game/room/room_user.h"

#include "database/queries/player_query.h"
#include "server/server_listener.h"

#include "util/stringbuilder.h"


/**
 * Sends the key of an error, whose description value is inside the external_texts of the client.
 *
 * @param p the player
 * @param error the error message
 */
void player_send_localised_error(entity *p, char *error) {
    outgoing_message *om = om_create(33); // @a
    sb_add_string(om->sb, error);
    player_send(p, om);
    om_cleanup(om);
}

/**
 * Send an alert to the player
 *
 * @param p the player
 * @param text the alert message
 */
void player_send_alert(entity *p, char *text) {
    outgoing_message *alert = om_create(139); // BK
    om_write_str(alert, text);
    player_send(p, alert);
    om_cleanup(alert);
}

/**
 * Send credit amount to player.
 *
 * @param player the player to send to
 */
void player_refresh_credits(entity *player) {
    char credits_string[10 + 1]; ///"num + /0";
    sprintf(credits_string, "%i", player->details->credits);

    outgoing_message *credits = om_create(6); // "@F"
    om_write_str(credits, credits_string);
    sb_add_string(credits->sb, ".0");
    player_send(player, credits);
    om_cleanup(credits);
}

/**
 * Send ticket amount to player.
 *
 * @param player the player to send to
 */
void player_refresh_tickets(entity *player) {
    char credits_string[10 + 1]; ///"num + /0";
    sprintf(credits_string, "%i", player->details->tickets);

    outgoing_message *credits = om_create(124); // "A|"
    sb_add_string(credits->sb, credits_string);
    player_send(player, credits);
    om_cleanup(credits);
}

/*
 * Refreshes user appearance
 *
 * @param player to refresh
 */
void player_refresh_appearance(entity *player) {
    entity_data *new_data = player_query_data(player->details->id);

    // Reload figure, gender and motto
    log_debug("Figure: %s, sex: %s, motto: %s", new_data->figure, new_data->sex, new_data->motto);
    player->details->figure = strdup(new_data->figure);
    player->details->sex = strdup(new_data->sex);
    player->details->motto = strdup(new_data->motto);

    player_data_cleanup(new_data);

    // Send refresh to user
    outgoing_message *user_info = om_create(5); // "@E
    om_write_str_int(user_info, player->details->id);
    om_write_str(user_info, player->details->username);
    om_write_str(user_info, player->details->figure);
    om_write_str(user_info, player->details->sex);
    om_write_str(user_info, player->details->motto);
    om_write_int(user_info, player->details->tickets);
    om_write_str(user_info, player->details->pool_figure); // pool figure
    om_write_int(user_info, player->details->film);
    player_send(player, user_info);
    om_cleanup(user_info);

    // Send refresh to room if inside room
    if (player->room_user != NULL && player->room_user->room != NULL) {
        outgoing_message *poof = om_create(266); // DJ"
        om_write_int(poof, player->room_user->instance_id);
        om_write_str(poof, player->details->figure);
        om_write_str(poof, player->details->sex);
        om_write_str(poof, player->details->motto);
        room_send(player->room_user->room, poof);
        om_cleanup(poof);
    }
}


/*
 * Refreshes user badges
 *
 * @param player to refresh
 */
void player_refresh_badges(entity *player) {
    Array *badges = player_query_badges(player->details->id);

    // if (player->details->club_days_left > 0) {
    //     if (array_add(badges, "HC1") != CC_OK) {
    //         log_fatal("Couldn't add HC1 to array in player_refresh_badges");
    //     }
    //
    //     // If the user has been subscribed for more than 11 months, give out the gold HC badge :)
    //     if (player->details->club_months_expired > 11) {
    //         if (array_add(badges, "HC2") != CC_OK) {
    //             log_fatal("Couldn't add HC2 to array in player_refresh_badges");
    //         }
    //     }
    // }

    outgoing_message *badge_list = om_create(229); // "Ce"
    om_write_int(badge_list, (int)array_size(badges));

    ArrayIter ai;
    array_iter_init(&ai, badges);

    int badge_slot = 0;
    int slot_counter = 0;
    void *next;

    while (array_iter_next(&ai, &next) != CC_ITER_END) {
        char *badge = (char*)next;
        om_write_str(badge_list, badge);

        if (strcmp(badge, player->details->badge) == 0) {
            badge_slot = slot_counter;
        }

        slot_counter++;
    }

    om_write_int(badge_list, badge_slot);
    om_write_int(badge_list, player->details->badge_active);

    player_send(player, badge_list);
    om_cleanup(badge_list);

    array_destroy(badges);
}


