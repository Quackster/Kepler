#ifndef ROOM_H
#define ROOM_H

#include <stdbool.h>
#include "lib/dispatch/dispatch.h"

typedef struct room_user_s room_user;
typedef struct coord_s coord;
typedef struct list_s List;
typedef struct entity_s entity;
typedef struct room_model_s room_model;
typedef struct outgoing_message_s outgoing_message;
typedef struct runnable_s runnable;
typedef struct room_map_s room_map;

typedef struct rights_entry_s {
    int user_id;
} rights_entry;

typedef struct room_data_s {
    int id;
    int owner_id;
    char *owner_name;
    int category;
    char *name;
    char *description;
    char *model;
    room_model *model_data;
    char *ccts;
    int wallpaper;
    int floor;
    int show_name;
    bool superusers;
    int accesstype;
    char *password;
    int visitors_now;
    int visitors_max;
} room_data;

typedef struct room_s room;

typedef struct room_s {
    int room_id;
    room *connected_room;
    bool connected_room_hide;
    struct room_data_s *room_data;
    room_map *room_map;
    hh_dispatch_timer_t *walk_process_timer;
    hh_dispatch_timer_t *status_process_timer;
    hh_dispatch_timer_t *roller_process_timer;
    List *users;
    List *items;
    List *rights;
    unsigned long tick;
} room;

room *room_create(int);
room_data *room_create_data(room*, int, int, int, char*, char*, char*, char*, int, int, int, bool, int, char*, int, int);
rights_entry *rights_entry_create(int user_id);
rights_entry *rights_entry_find(room *room, int user_id);
void room_append_data(room *instance, outgoing_message *navigator, entity *player);
void room_load_data(room *room);
void room_kickall(room*);
bool room_is_owner(room *room, int user_id);
bool room_has_rights(room *room, int user_id);
void room_refresh_rights(room *room, entity *player);
void room_send(room *room, outgoing_message *message);
bool room_send_with_rights(room *room, outgoing_message *message);
void room_dispose(room*, bool force_dispose);
List *room_nearby_players(room *room, room_user *room_user, coord *position, int distance);
outgoing_message *room_lingo_command(char *command);


#endif