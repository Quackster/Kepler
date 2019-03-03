#include <stdlib.h>
#include <ctype.h>

#include "room_model.h"
#include "shared.h"
#include "list.h"

#include "game/items/item.h"
#include "game/items/items_data_parser.h"

#include "game/navigator/navigator_category_manager.h"

/**
 *
 * @param modeltype
 * @param door_x
 * @param door_y
 * @param door_z
 * @param heightmap
 * @return
 */
room_model *room_model_create(char *model_id, char *model_name, int door_x, int door_y, double door_z, int door_dir, char *heightmap) {
    room_model *model = malloc(sizeof(room_model));
    model->model_id = strdup(model_id);
    model->model_name = strdup(model_name);
    model->door_x = door_x;
    model->door_y = door_y;
    model->door_z = door_z;
    model->door_dir = door_dir;
    model->map_size_x = 0;
    model->map_size_y = 0;
    model->heightmap = replace(heightmap, "|", "\r");
    model->public_items = NULL;

    List *items = item_parser_get_items(model->model_id);

    if (items != NULL) {
        model->public_items = items;
    } else {
        list_new(&model->public_items);
    }
    
    room_model_parse(model);
    return model;
}

/**
 * Parse heightmap, will grab height and enterable/non-enterable tiles
 *
 * @param room_model the room model struct to parse
 */
void room_model_parse(room_model *room_model) {
    char *heightmap = strdup(room_model->heightmap);
    char *array[100];

    int lines = 0;

    array[lines] = strtok(heightmap,"\r");

    while(array[lines] != NULL) {
        array[++lines] = strtok(NULL,"\r");
    }

    int map_size_x = (int)strlen(array[0]);
    int map_size_y = lines;

    room_model->map_size_x = map_size_x;
    room_model->map_size_y = map_size_y;

    if (room_model->map_size_x >= 200 || room_model->map_size_y >= 200) {
        room_model->map_size_x = 200 - 1;
        room_model->map_size_y = 200 - 1;
    }

     for (int y = 0; y < map_size_y; y++) {
        char *line = array[y];

        for (int x = 0; x < strlen(line); x++) {
            char ch = (char)tolower(line[x]);

            if (ch == 'x') {
                room_model->states[x][y] = CLOSED;
                room_model->heights[x][y] = 0;
            } else {
                int height = ch - '0';
                room_model->states[x][y] = OPEN;
                room_model->heights[x][y] = height;
            }
            
            if (x == room_model->door_x && y == room_model->door_y) {
                room_model->states[x][y] = OPEN;
                room_model->heights[x][y] = room_model->door_z;
            }
        }
     }

    free(heightmap);
}

/**
 * Dispose room model.
 *
 * @param model the model to dispose.
 */
void room_model_dispose(room_model *model) {
    free(model->model_id);
    free(model->model_name);
    free(model->heightmap);
    list_destroy(model->public_items);
    free(model);
}