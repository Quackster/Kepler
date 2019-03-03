#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "database/queries/rooms/room_vote_query.h"

void RATEFLAT(entity *player, incoming_message *im) {
    if (player->room_user->room == NULL) {
        return;
    }

    int answer = im_read_vl64(im);

    if (answer != 1 && answer != -1) {
        return;
    }

    int room_id = player->room_user->room->room_data->id;
    int player_id = player->details->id;

    // Check if already voted, return if voted
    int NOT_VOTED = -1;
    if (room_query_check_voted(room_id, player_id) != NOT_VOTED) {
        return;
    }

    room_query_vote(room_id, player_id, answer);

    int votes = room_query_count_votes(room_id);

    outgoing_message *om = om_create(345); // "EY"
    om_write_int(om, votes);
    player_send(player, om);
    om_cleanup(om);

    room *room = player->room_user->room;

    // TODO: optimize this by not doing queries at all (room should have map of who voted)
    // Send new vote count only to users who have voted
    // because else their vote selector UI disappears
    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *room_player;
        list_get_at(room->users, i, (void*)&room_player);

        player_id = room_player->details->id;

        if (room_query_check_voted(room_id, player_id) == NOT_VOTED) {
            continue;
        }

        om = om_create(345); // "EY"
        om_write_int(om, votes);
        player_send(room_player, om);
        om_cleanup(om);
    }

    // TODO: loop through all rooms users and send new vote count to users who haven't voted yet
}
