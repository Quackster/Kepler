#ifndef ROOM_MODEL_MANAGER_H
#define ROOM_MODEL_MANAGER_H

typedef struct list_s List;
typedef struct room_model_s room_model;

struct room_model_manager {
    List *models;
};

void model_manager_init();
room_model *model_manager_get(char *model);
void model_manager_dispose();

#endif