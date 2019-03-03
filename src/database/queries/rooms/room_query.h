#ifndef ROOM_QUERY_H
#define ROOM_QUERY_H

typedef struct list_s List;
typedef struct hashtable_s HashTable;

typedef struct room_s room;
typedef struct room_data_s room_data;

List *room_query_get_by_owner_id(int);
room *room_query_get_by_room_id(int);
List *room_query_search(char *search_query);
List *room_query_recent_rooms(int limit, int category_id);
List *room_query_random_rooms(int limit);

List *room_query_get_models();
room_data *room_create_data_sqlite(room *room, sqlite3_stmt *stmt);
void room_query_get_categories();
void query_room_save(room*);
void room_query_delete(int room_id);

#endif