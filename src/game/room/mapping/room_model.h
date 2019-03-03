#ifndef ROOM_MODEL_H
#define ROOM_MODEL_H

typedef struct list_s List;

typedef enum {
    CLOSED,
    OPEN,
} room_title_states;

typedef struct room_model_s {
    char *model_id;
    char *model_name;
    int door_x;
    int door_y;
    double door_z;
    int door_dir;
    char *heightmap;
    List *public_items;
    int map_size_x;
    int map_size_y;
    room_title_states states[200][200];
    double heights[200][200];
} room_model;


room_model *room_model_create(char*, char *, int, int, double, int, char *);
void room_model_parse(room_model*);
void room_model_dispose(room_model *model);

#endif