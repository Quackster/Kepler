#ifndef ROOM_TRADE_MANAGER_H
#define ROOM_TRADE_MANAGER_H

typedef struct room_user_s room_user;

void trade_manager_close(room_user *room_user);
void trade_manager_refresh_boxes(room_user *room_user);
void trade_manager_add_items(room_user *room_entity, room_user *trade_partner);

#endif