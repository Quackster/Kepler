#ifndef SHARED_H
#define SHARED_H

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include "sqlite3.h"

#include "game/items/item_manager.h"
#include "game/player/player_manager.h"
#include "game/room/mapping/room_model_manager.h"
#include "game/room/room_manager.h"
#include "game/room/public_rooms/walkways.h"
#include "game/catalogue/catalogue_manager.h"
#include "game/texts/external_texts_manager.h"
#include "game/navigator/navigator_category_manager.h"
#include "game/moderation/fuserights_manager.h"

#include "util/configuration/configuration.h"

#include "uv.h"

#define PREFIX "Kepler"
#define AARON_IS_A_FAG 8934

typedef struct sqlite3 sqlite3;

typedef struct server_s {
    struct player_manager player_manager;
    struct room_manager room_manager;
    struct room_model_manager room_model_manager;
    struct room_category_manager room_category_manager;
    struct catalogue_manager catalogue_manager;
    struct item_manager item_manager;
    struct texts_manager texts_manager;
    struct configuration configuration;
    struct walkway_manager walkway_manager;
    struct fuserights_manager fuserights_manager;
    struct sqlite3 *DB;
    uv_loop_t *rcon_loop;
    bool is_shutdown;
} server;

server global;

char *get_time_formatted();
char *get_short_time_formatted();
char *get_time_formatted_custom(unsigned long);
void filter_vulnerable_characters(char**, bool);
char *replace_unreadable_characters(char*);
char *get_argument(char*, char*, int);
char* replace(char* str, char* a, char* b);
char *replace_char(const char *, char, char *);
int valid_password(const char*, const char*);
int get_name_check_code(char*);
bool is_numeric(const char*);
bool has_numbers(const char*);
bool has_allowed_characters(char *, char *);
bool starts_with(const char *pre, const char *str);
size_t get_file_line(char **lineptr, size_t *n, FILE *stream);

#endif