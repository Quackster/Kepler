#ifndef ROOM_VOTE_QUERY_H
#define ROOM_VOTE_QUERY_H

int room_query_check_voted(int room_id, int player_id);
void room_query_vote(int room_id, int player_id, int answer);
int room_query_count_votes(int room_id);

#endif