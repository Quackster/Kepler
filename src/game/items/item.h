#ifndef ITEM_H
#define ITEM_H

#include <shared.h>

typedef struct room_s room;
typedef struct coord_s coord;
typedef struct item_definition_s item_definition;

typedef struct item_s {
    int id;
    int room_id;
    int owner_id;
    coord *position;
    item *item_below;
    char *wall_position;
    char *custom_data;
    char *current_program;
    char *current_program_state;
    item_definition *definition;
} item;

item *item_create(int id, int room_id, int owner_id, int definition_id, int x, int y, double z, char *wall_position, int rotation, char *custom_data);
void item_update_entities(item *item, room *room, coord *old_position);
void item_set_custom_data(item *item, char *custom_data);
void item_broadcast_custom_data(item* item, char *custom_data);
bool item_is_walkable(item *item);
char *item_as_string(item *item);
char *item_strip_string(item *item, int strip_slot_id);
void item_assign_program(item*, char*);
double item_total_height(item *item);
void item_dispose(item *item);

#endif