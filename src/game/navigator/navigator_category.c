#include <stdlib.h>
#include <string.h>

#include "game/room/room.h"

#include "communication/messages/outgoing_message.h"
#include "game/player/player.h"

#include "navigator_category.h"

/**
 * Create a navigator category.
 *
 * @param id the category id
 * @param parent_id the parent id
 * @param name the name of the category
 * @param public_spaces the public spaces
 * @param allow_trading does this category allow trading
 * @return the created navigator navigator
 */
room_category *category_create(int id, int parent_id, char *name, int public_spaces, int allow_trading, int minrole_access, int minrole_setflatcat) {
    room_category *category = malloc(sizeof(room_category));
    category->id = id;
    category->parent_id = parent_id;
    category->name = strdup(name);
    category->public_spaces = public_spaces;
    category->allow_trading = allow_trading;

    if (public_spaces == 0) {
        category->category_type = PRIVATE;
    } else {
        category->category_type = PUBLIC;
    }

    category->minrole_access = minrole_access;
    category->minrole_setflatcat = minrole_setflatcat;

    return category;
}

/**
 * Gets if the user has permission to see this category.
 *
 * @param category the category to check
 * @param rank the rank
 * @return true, if successful
 */
int category_has_access(room_category *category, int rank) {
    return rank >= category->minrole_access;
}