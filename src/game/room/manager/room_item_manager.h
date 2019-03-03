#ifndef ROOM_ITEM_MANAGER_H
#define ROOM_ITEM_MANAGER_H

typedef struct room_s room;
typedef struct item_s item;
typedef struct list_s List;

List *room_item_manager_floor_items(room *room);
List *room_item_manager_wall_items(room *room);
List *room_item_manager_public_items(room *room);
item *room_item_manager_get(room *room, int item_id);
void room_item_manager_load(room *room);
void room_item_manager_dispose(room *room);

#endif