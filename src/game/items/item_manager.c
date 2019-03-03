#include "shared.h"

#include "database/queries/items/item_query.h"
#include "database/queries/items/furniture_query.h"

#include "item_manager.h"
#include "item.h"

/**
 * Load item definitions.
 */
void item_manager_init() {
    global.item_manager.definitions = furniture_query_definitions();
    global.item_manager.sprite_index = om_create(295); // "Dg";
    om_write_int(global.item_manager.sprite_index, 0);
    //om_write_int(global.item_manager.sprite_index, (int)hashtable_size(global.item_manager.definitions));

    /*HashTableIter iter;
    TableEntry *entry;

    hashtable_iter_init(&iter, global.item_manager.definitions);

    while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
        item_definition *def = entry->value;

        om_write_str(global.item_manager.sprite_index, def->sprite);
        om_write_int(global.item_manager.sprite_index, def->cast_directory);
    }*/
}

/**
 * Delete the item.
 *
 * @param item
 */
void item_manager_delete(item *item) {
    item_query_delete(item->room_id);
    item_dispose(item);
}

/**
 * Get item definition by id
 *
 * @param room_id the definition id
 * @return the item definition
 */
item_definition *item_manager_get_definition_by_id(int definition_id) {
    item_definition *definition = NULL;

    if (hashtable_contains_key(global.item_manager.definitions, &definition_id)) {
        hashtable_get(global.item_manager.definitions, &definition_id, (void *)&definition);
    }

    return definition;
}

void item_manager_dispose() {
    if (hashtable_size(global.item_manager.definitions) > 0) {
        HashTableIter iter;
        hashtable_iter_init(&iter, global.item_manager.definitions);

        TableEntry *entry;
        while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
            item_definition *def = entry->value;
            item_definition_dispose(def);
        }
    }

    hashtable_destroy(global.item_manager.definitions);
    om_cleanup(global.item_manager.sprite_index);
}