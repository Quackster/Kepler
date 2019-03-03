#ifndef ITEM_MANAGER_H
#define ITEM_MANAGER_H

#include "hashtable.h"
#include "game/items/definition/item_definition.h"
#include "communication/messages/outgoing_message.h"

typedef struct item_s item;

struct item_manager {
    HashTable *definitions;
    outgoing_message *sprite_index;
};

void item_manager_init();
void item_manager_delete(item *item);
item_definition *item_manager_get_definition_by_id(int definition_id);
void item_manager_dispose();

#endif