#include "shared.h"
#include "list.h"

#include "database/queries/rooms/room_query.h"

#include "game/items/item.h"
#include "game/items/items_data_parser.h"

#include "room_model.h"

/**
 * Initialise the model manager.
 */
void model_manager_init() {
    global.room_model_manager.models = room_query_get_models();
}

/**
 * Add a model, if it already exists, it won't be added
 * 
 * @param model the model id
 */
void model_manager_add(room_model *model) {
    if (model_manager_get(model->model_id) != NULL) {
        return;
    }

    list_add(global.room_model_manager.models, model);
}

/** 
 * Get model by model id.
 * 
 * @param model_id the model id to find
 * @return the room model struct
 */
room_model *model_manager_get(char *model_id) {
    ListIter iter;
    list_iter_init(&iter, global.room_model_manager.models);

    room_model *model;

    while (list_iter_next(&iter, (void*) &model) != CC_ITER_END) {
        if (strcmp(model->model_id, model_id) == 0) {
            return model;
        }
    }


    return NULL;
}

/**
 * Dispose model manager
 */
void model_manager_dispose() {
    for (size_t i = 0; i < list_size(global.room_model_manager.models); i++) {
        room_model *model;
        list_get_at(global.room_model_manager.models, i, (void *) &model);
        room_model_dispose(model);
    }

    list_destroy(global.room_model_manager.models);
}