#ifndef NAVIGATOR_CATEGORY_H
#define NAVIGATOR_CATEGORY_H

typedef enum room_category_type_e {
    PUBLIC,
    PRIVATE
} room_category_type;

typedef struct room_category_s {
    int id;
    int parent_id;
    char *name;
    int public_spaces;
    int allow_trading;
    room_category_type category_type;
    int minrole_access;
    int minrole_setflatcat;
} room_category;

room_category *category_create(int, int, char*, int, int, int, int);
int category_has_access(room_category *category, int rank);
void category_serialise(outgoing_message*, room*, room_category_type, entity*);

#endif