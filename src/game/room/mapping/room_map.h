#ifndef ROOM_MAP_H
#define ROOM_MAP_H

#include "game/room/room.h"

typedef struct list_s List;
typedef struct room_tile_s room_tile;
typedef struct coord_s coord;

typedef struct room_map_s {
    room_tile *map[200][200];
} room_map;

void room_map_init(room *);
void room_map_regenerate(room *);
void room_map_add_item(room *room, item *item);
void room_map_move_item(room *room, item *item, bool rotation, coord *old_position);
void room_map_remove_item(room *room, item *item);
void room_map_item_adjustment(room *room, item *adjusted_item, bool rotation);
void room_map_destroy(room*);


#endif