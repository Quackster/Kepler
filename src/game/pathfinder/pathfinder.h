#ifndef PATHFINDER_H
#define PATHFINDER_H

#include "game/room/room_user.h"

typedef struct node_s node;
typedef struct deque_s Deque;
typedef struct coord_s coord;

typedef struct pathfinder_s {
    node *map[200][200];
    Deque *open_list;
    node *current;
    node *nodes;
    int failed;
} pathfinder;

Deque *create_path(room_user*);
pathfinder *make_path_reversed(room_user*, int, int);
int is_valid_tile(room_user*, coord from, coord to, bool is_final_move);

#endif