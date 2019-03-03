#include <game/room/room_user.h>
#include "stdlib.h"

#include "hashtable.h"
#include "list.h"

#include "game/pathfinder/coord.h"

#include "walkways.h"
#include "shared.h"

void walkways_init() {
    list_new(&global.walkway_manager.walkways);
    walkways_add("rooftop", "rooftop_2", "9,4 10,3 9,3", NULL, false);
    walkways_add("rooftop_2", "rooftop", "3,10 4,10 5,10 3,11 4,11 5,11", "10,5,4,4", true);

    walkways_add("old_skool0", "old_skool1", "16,18", NULL, false);
    walkways_add("old_skool1", "old_skool0", "0,7", "15,18,0,6", true);

    walkways_add("malja_bar_a", "malja_bar_b", "14,0 15,0", NULL, false);
    walkways_add("malja_bar_b", "malja_bar_a", "5,25 ", "15,1,4,4", true);

    walkways_add("bar_a", "bar_b", "9,32 10,32 11,32 9,33 10,33", NULL, false);
    walkways_add("bar_b", "bar_a", "1,10 1,11 1,12", "10,30,5,0", true);

    walkways_add("pool_a", "pool_b", "19,3 20,4 21,5 22,6 23,7 24,8 25,9 26,10 27,11 28,12", NULL, false);
    walkways_add("pool_b", "pool_a", "0,13 1,14 2,15 3,16 4,17 5,18 6,19 7,20 8,21 9,22 10,23 11,24 12,25", "23,7,7,5", true);

    walkways_add("hallway2", "hallway0", "0,6 0,7 0,8 0,9", "29,3,1,6", false);
    walkways_add("hallway2", "hallway3", "6,23 7,23 8,23 9,23", "7,2,1,4", false);
    walkways_add("hallway2", "hallway4", "27,6 27,7 27,8 27,9", "2,3,0,2", false);
    walkways_add("hallway0", "hallway2", "31,5 31,4 31,3 31,2", "2,7,1,2", true);
    walkways_add("hallway0", "hallway1", "14,19 15,19 16,19 17,19", "15,2,0,4", true);
    walkways_add("hallway1", "hallway3", "31,9 31,8 31,7 31,6", "2,8,1,2", true);
    walkways_add("hallway1", "hallway0", "17,0 16,0 15,0 14,0", "16,17,1,0", true);
    walkways_add("hallway3", "hallway2", "9,0 8,0 7,0 6,0", "8,21,1,0", true);
    walkways_add("hallway3", "hallway1", "0,9 0,8 0,7 0,6", "29,7,0,6", true);
    walkways_add("hallway3", "hallway5", "31,6 31,7 31,8 31,9", "2,15,0,2", true);
    walkways_add("hallway5", "hallway3", "0,17 0,16 0,15 0,14", "29,7,0,6", true);
    walkways_add("hallway5", "hallway4", "22,0 23,0 24,0 25,0", "24,17,1,0", true);
    walkways_add("hallway4", "hallway2", "0,2 0,3 0,4 0,5", "25,7,0,6", true);
    walkways_add("hallway4", "hallway5", "22,19 23,19 24,19 25,19", "24,2,1,4", true);
}

void walkways_add(char *model_from, char *model_to, char *from_coords, char *destination, bool hide_room) {
    walkway_entrance *walkway = malloc(sizeof(walkway_entrance));
    walkway->model_from = strdup(model_from);
    walkway->model_to = strdup(model_to);
    walkway->from_coords = walkways_get_coords(from_coords);
    walkway->hide_room = hide_room;

    if (destination != NULL) {
        walkway->destination = coord_create(0, 0);

        char *str_x = get_argument(destination, ",", 0);
        char *str_y = get_argument(destination, ",", 1);
        char *str_z = get_argument(destination, ",", 2);
        char *str_r = get_argument(destination, ",", 3);

        walkway->destination->x = (int) strtol(str_x, NULL, 10);
        walkway->destination->y = (int) strtol(str_y, NULL, 10);
        walkway->destination->z = (int) strtol(str_z, NULL, 10);
        walkway->destination->head_rotation = (int) strtol(str_r, NULL, 10);
        walkway->destination->body_rotation = (int) strtol(str_r, NULL, 10);

        free(str_x);
        free(str_y);
        free(str_z);
        free(str_r);

    } else {
        walkway->destination = NULL;
    }

    list_add(global.walkway_manager.walkways, walkway);
}

walkway_entrance *walkways_find_current(room_user *room_user) {
    if (room_user->room->room_data->owner_id > 0) {
        return NULL;
    }

    for (size_t i = 0; i < list_size(global.walkway_manager.walkways); i++) {
        walkway_entrance *entrance;
        list_get_at(global.walkway_manager.walkways, i, (void *) &entrance);

        if (strcmp(room_user->room->room_data->model, entrance->model_from) == 0) {
            for (size_t j = 0; j < list_size(entrance->from_coords); j++) {
                coord *coord;
                list_get_at(entrance->from_coords, j, (void *) &coord);

                if (coord->x == room_user->position->x && coord->y == room_user->position->y) {
                    return entrance;
                }
            }
        }
    }

    return NULL;
}

room *walkways_find_room(char *model) {
    if (hashtable_size(global.room_manager.rooms) == 0) {
        return NULL;
    }

    HashTableIter iter;
    hashtable_iter_init(&iter, global.room_manager.rooms);

    TableEntry *entry;
    while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
        room *room = entry->value;

        if (strcmp(room->room_data->model, model) == 0) {
            return room;
        }
    }

    return NULL;
}

List *walkways_get_coords(char *coords) {
    List *coordinates;
    list_new(&coordinates);

    int i = 0;

    while (true) {
        char *coord = get_argument(coords, " ", i++);

        if (coord == NULL) {
            break;
        }

        char *str_x = get_argument(coord, ",", 0);
        char *str_y = get_argument(coord, ",", 1);

        int x = (int) strtol(str_x, NULL, 10);
        int y = (int) strtol(str_y, NULL, 10);

        list_add(coordinates, coord_create(x, y));

        free(coord);
        free(str_x);
        free(str_y);
    }

    return coordinates;
}