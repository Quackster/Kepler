#ifndef PLAYER_MANAGER_H
#define PLAYER_MANAGER_H

typedef struct list_s List;
typedef struct entity_s entity;
typedef struct entity_details_s entity_data;

struct player_manager {
    List *players;
};

void player_manager_init();
entity *player_manager_add(void*, char *ip);
void player_manager_remove(entity*);
entity *player_manager_find_by_name(char *name);
entity *player_manager_find_by_id(int);
entity_data *player_manager_get_data_by_id(int);
void player_manager_destroy_session_by_id(int player_id);
void player_manager_dispose();

#endif