#ifndef NAVIGATOR_CATEGORY_MANAGER_H
#define NAVIGATOR_CATEGORY_MANAGER_H

typedef struct list_s List;
typedef struct hashtable_s HashTable;
typedef struct outgoing_message_s outgoing_message;
typedef struct room_s room;
typedef struct entity_s entity;
typedef struct room_category_s room_category;

typedef enum room_category_type_e room_category_type;

struct room_category_manager {
    HashTable *categories;
};

void category_manager_init();
void category_manager_add(room_category*);
room_category *category_manager_get_by_id(int);
List *category_manager_flat_categories();
List *category_manager_get_by_parent_id(int);
List *category_manager_get_rooms(int);
int category_manager_get_current_vistors(int);
int category_manager_get_max_vistors(int);
void category_manager_dispose();

#endif