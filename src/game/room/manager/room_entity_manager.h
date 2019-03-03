#ifndef ROOM_ENTITY_MANAGER_H
#define ROOM_ENTITY_MANAGER_H

typedef struct room_s room;
typedef struct room_user_s room_user;
typedef struct entity_s entity;
typedef struct coord_s coord;
typedef struct outgoing_message_s outgoing_message;

int create_instance_id(room_user*);
room_user *room_user_get_by_instance_id(room *, int);

void room_enter(room*, entity*, coord *destination);
void room_leave(room*, entity*, bool hotel_view);

void append_user_list(outgoing_message *players, entity *player);
void append_user_status(outgoing_message *om, entity *player);

#endif