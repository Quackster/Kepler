#include "coord.h"

#include "stdio.h"
#include "stdlib.h"

/**
 * Create a coordinate struct with given default parameters
 *
 * @param x the x coordinate
 * @param y the y coordinate
 * 
 * @return coord ptr
 */
coord *coord_create(int x, int y) {
    coord *pos = malloc(sizeof(coord));
    pos->x = x;
    pos->y = y;
    pos->z = 0;
    pos->head_rotation = 0;
    pos->body_rotation = 0;
    pos->rotation = 0;
    return pos;
}

/**
 * Create a coordinate struct with given default parameters
 *
 * @param x the x coordinate
 * @param y the y coordinate
 * @param z the z coordinate
 *
 * @return coord ptr
 */
coord *create_coord_height(int x, int y, double z) {
    coord *pos = malloc(sizeof(coord));
    pos->x = x;
    pos->y = y;
    pos->z = z;
    pos->head_rotation = 0;
    pos->body_rotation = 0;
    pos->rotation = 0;
    return pos;
}

/**
 * Get the distance squared between two points.
 * 
 * @param first the first coordinate to compare
 * @param second the second coordinate to compare
 * 
 * @return distance
 */
int coord_distance_squared(coord *first, coord *second) {
    int dx = first->x - second->x;
    int dy = first->y - second->y;
    return (dx * dx) + (dy * dy);
}

/**
 * Apply rotation to coord struct.
 *
 * @param coord the coord to set
 * @param head_rotation the head rotation
 * @param body_rotation the body rotation
 */
void coord_set_rotation(coord *coord, int head_rotation, int body_rotation) {
    coord->rotation = body_rotation;
    coord->body_rotation = body_rotation;
    coord->head_rotation = head_rotation;
}

/**
 * Get the square in front of the square specified parameter, and set the
 * variables in the second parameter.
 *
 * @param start_coord the first square
 * @param new_coord the new square
 */
void coord_get_front(coord *start_coord, coord *new_coord) {
    new_coord->x = start_coord->x;
    new_coord->y = start_coord->y;
    new_coord->z = start_coord->z;
    new_coord->rotation = start_coord->rotation;

    if (new_coord->rotation == 0) {
        new_coord->y--;
    } else if (new_coord->rotation == 2) {
        new_coord->x++;
    } else if (new_coord->rotation == 4) {
        new_coord->y++;
    } else if (new_coord->rotation == 6) {
        new_coord->x--;
    }
}


/**
 * Get the square left of the square specified parameter, and set the
 * variables in the second parameter.
 *
 * @param start_coord the first square
 * @param new_coord the new square
 */
void coord_get_left(coord *start_coord, coord *new_coord) {
    new_coord->x = start_coord->x;
    new_coord->y = start_coord->y;
    new_coord->z = start_coord->z;
    new_coord->rotation = start_coord->rotation;

    if (new_coord->rotation == 0) {
        new_coord->x++;
    } else if (new_coord->rotation == 2) {
        new_coord->y--;
    } else if (new_coord->rotation == 4) {
        new_coord->x--;
    } else if (new_coord->rotation == 6) {
        new_coord->y--;
    }
}
