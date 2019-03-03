#include "list.h"

#include "affected_tiles.h"
#include "coord.h"

List *get_affected_tiles(int item_length, int item_width, int x, int y, int rotation) {
    List *tiles;
    list_new(&tiles);

    if (item_length != item_width) {
        if (rotation == 0 || rotation == 4) {
            int l = item_length;
            int w = item_width;
            item_length = w;
            item_width = l;
        }
    }

    for (int new_x = x; new_x < x + item_width; new_x++) {
        for (int new_y = y; new_y < y + item_length; new_y++) {
            coord *coord = coord_create(new_x, new_y);
            list_add(tiles, coord);
        }
    }

    return tiles;
}