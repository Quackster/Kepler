#ifndef ROOM_FAVOURITES_QUERY_H
#define ROOM_FAVOURITES_QUERY_H

typedef struct list_s List;

int room_query_check_favourite(int room_id, int player_id);
void room_query_favourite(int room_id, int player_id);
void room_query_remove_favourite(int room_id, int player_id);
List *room_query_favourites(int player_id);

#endif