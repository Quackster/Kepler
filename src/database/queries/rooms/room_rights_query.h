#ifndef ROOM_RIGHTS_QUERY_H
#define ROOM_RIGHTS_QUERY_H

typedef struct list_s List;

void room_query_add_rights(int room_id, int player_id);
void room_query_remove_rights(int room_id, int player_id);
List *room_query_rights(int room_id);

#endif