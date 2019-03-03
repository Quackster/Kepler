#ifndef AFFECTED_TILES_H
#define AFFECTED_TILES_H

#include <shared.h>

typedef struct list_s List;

List *get_affected_tiles(int item_length, int item_width, int x, int y, int rotation);

#endif