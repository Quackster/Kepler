#ifndef CATALOGUE_ITEM_H
#define CATALOGUE_ITEM_H

#include <stdbool.h>

#include "game/items/definition/item_definition.h"

typedef struct list_s List;

typedef struct catalogue_item_s {
    char *sale_code;
    int page_id;
    int order_id;
    int price;
    int definition_id;
    int item_specialspriteid;
    char *package_name;
    char *package_description;
    bool is_package;
    item_definition *definition;
    List *packages;
} catalogue_item;

catalogue_item *catalogue_item_create(char *sale_code, int page_id, int order_id, int price, int definition_id, int item_specialspriteid, char *package_name, char *package_description, bool is_package);
char *catalogue_item_get_name(catalogue_item *item);
char *catalogue_item_get_description(catalogue_item *item);
char *catalogue_item_get_type(catalogue_item *item);
char *catalogue_item_get_icon(catalogue_item *item);
char *catalogue_item_get_size(catalogue_item *item);
char *catalogue_item_get_dimensions(catalogue_item *item);
void catalogue_item_dispose(catalogue_item *item);

#endif