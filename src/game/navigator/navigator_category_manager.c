#include "shared.h"

#include "list.h"
#include "hashtable.h"

#include "game/room/room.h"
#include "database/queries/rooms/room_query.h"

#include "navigator_category.h"
#include "navigator_category_manager.h"

/**
 * Sort
 *
 * @param e1 the first category
 * @param e2 the second category
 * @return whether to sort
 */
int category_manager_compare(void const *e1, void const *e2) {
    room_category *i = (*((room_category**) e1));
    room_category *j = (*((room_category**) e2));

    if (i->id < j->id) {
        return -1;
    }

    if (i->id == j->id) {
        return 0;
    }

    return 1;
}

/**
 * Navigator category manager
 */
void category_manager_init() {
    hashtable_new(&global.room_category_manager.categories);
    room_query_get_categories();
}

/**
 * Add a navigator category.
 * 
 * @param category the category struct
 */
void category_manager_add(room_category *category) {
    hashtable_add(global.room_category_manager.categories, &category->id, category);
}

/**
 * Get navigator category by the category id.
 * 
 * @param category_id the category id
 * @return the room category
 */
room_category *category_manager_get_by_id(int category_id) {
    void *category = NULL;

    if (hashtable_contains_key(global.room_category_manager.categories, &category_id)) {
        hashtable_get(global.room_category_manager.categories, &category_id, (void *)&category);
    }

    return category;
}

/**
 * Get all the flat categories belonging to rooms owned by users.
 * 
 * @return the list of flat categories
 */
List *category_manager_flat_categories() {
    List *categories;
    list_new(&categories);

    HashTableIter iter;
    hashtable_iter_init(&iter, global.room_category_manager.categories);

    TableEntry *entry;
    while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
        room_category *category = entry->value;

        if (category->category_type == PRIVATE) {
            list_add(categories, category);
        }
    }

    list_sort_in_place(categories, category_manager_compare);
    return categories;
}

/**
 * Get child navigator categories by the parent category id.
 * 
 * @param category_id the category id
 * @return the list of room categories
 */
List *category_manager_get_by_parent_id(int category_id) {
    List *sub_categories;
    list_new(&sub_categories);

    HashTableIter iter;
    hashtable_iter_init(&iter, global.room_category_manager.categories);

    TableEntry *entry;
    while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
        room_category *category = entry->value;

        if (category->parent_id == category_id) {
            list_add(sub_categories, category);
        }
    }

    list_sort_in_place(sub_categories, category_manager_compare);
    return sub_categories;
}

/**
 * Get rooms by the category id.
 * 
 * @param category_id the category id
 * @return the list of rooms
 */
List *category_manager_get_rooms(int category_id) {
    List *rooms;
    list_new(&rooms);

    HashTableIter iter;
    hashtable_iter_init(&iter, global.room_manager.rooms);

    TableEntry *entry;
    while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
        room *instance = entry->value;

        if (instance->room_data->category == category_id) {
            list_add(rooms, instance);
        }
    }

    return rooms;
}

/**
 *
 * @param category_id
 * @return
 */
int category_manager_get_current_vistors(int category_id) {
    int current_visitors = 0;

    HashTableIter iter;
    hashtable_iter_init(&iter, global.room_manager.rooms);

    TableEntry *entry;
    while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
        room *instance = entry->value;

        if (instance->room_data->category == category_id) {
            current_visitors += instance->room_data->visitors_now;

            /**
             * Recursive lisiting for child categories underneath this category
             */
            List *categories = category_manager_get_by_parent_id(category_id);

            ListIter list_iter;
            list_iter_init(&list_iter, categories);

            room_category *category;

            while (list_iter_next(&list_iter, (void*) &category) != CC_ITER_END) {
                current_visitors += category_manager_get_current_vistors(category->id);
            }

            list_destroy(categories);
        }
    }

    return current_visitors;
}

/**
 *
 * @param category_id
 * @return
 */
int category_manager_get_max_vistors(int category_id) {
    int max_visitors = 0;

    HashTableIter iter;
    hashtable_iter_init(&iter, global.room_manager.rooms);

    TableEntry *entry;
    while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
        room *instance = entry->value;

        if (instance->room_data->category == category_id) {
            max_visitors += instance->room_data->visitors_max;

            /**
             * Recursive lisiting for child categories underneath this category
             */
            List *categories = category_manager_get_by_parent_id(category_id);

            ListIter list_iter;
            list_iter_init(&list_iter, categories);

            room_category *category;

            while (list_iter_next(&list_iter, (void*) &category) != CC_ITER_END) {
                max_visitors += category_manager_get_max_vistors(category->id);
            }
            list_destroy(categories);
        }
    }

    return max_visitors;
}


/**
 * Dispose room manager.
 */
void category_manager_dispose() {
    if (hashtable_size(global.room_category_manager.categories) > 0) {
        HashTableIter iter;
        hashtable_iter_init(&iter, global.room_category_manager.categories);

        TableEntry *entry;
        while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
            room_category *category = entry->value;

            free(category->name);
            free(category);
        }
    }

    hashtable_destroy(global.room_category_manager.categories);
}