#ifndef WALKWAYS_H
#define WALKWAYS_H

typedef struct list_s List;

typedef struct coord_s coord;
typedef struct room_s room;
typedef struct room_user_s room_user;

struct walkway_manager {
    List *walkways;
};

typedef struct walkway_entrance_s {
    char *model_from;
    char *model_to;
    List *from_coords;
    coord *destination;
    bool hide_room;
} walkway_entrance;

void walkways_init();
void walkways_add(char *model_from, char *model_to, char *from_coords, char *destination, bool hide_room);
walkway_entrance *walkways_find_current(room_user *room_user);
room *walkways_find_room(char *model);
List *walkways_get_coords(char *coords);

#endif