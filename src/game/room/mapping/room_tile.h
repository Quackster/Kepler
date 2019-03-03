#ifndef ROOM_TILE_H
#define ROOM_TILE_H

#include "game/room/room.h"

typedef struct list_s List;
typedef struct room_user_s room_user;
typedef struct item_s item;

typedef struct room_tile_s {
    room_user *entity;
    item *highest_item;
    struct room_s *room;
    List *items;
    double tile_height;
    int x;
    int y;
} room_tile;

room_tile *room_tile_create(room *room, int x, int y);
void room_tile_reset(room_tile *tile, room *room);
bool room_tile_is_walkable(room *room, room_user *room_user, int x, int y);
void room_tile_add_item(room_tile*, item*);
void room_tile_destroy(room_tile*, room*);

#endif