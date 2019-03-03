#ifndef ROOM_MANAGER_H
#define ROOM_MANAGER_H

typedef struct list_s List;
typedef struct hashtable_s HashTable;
typedef struct room_s room;

struct room_manager {
    HashTable *rooms;
};

void room_manager_init();
void room_manager_add(int);
void room_manager_add_by_user_id(int);
List *room_manager_get_by_user_id(int);
room *room_manager_get_by_id(int);
void room_manager_remove(int);
void room_manager_load_connected_rooms();
int room_manager_sort_id(void const *e1, void const *e2);
int room_manager_sort(void const *e1, void const *e2);
void room_manager_dispose();

#endif