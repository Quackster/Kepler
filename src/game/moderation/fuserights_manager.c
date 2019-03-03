#include "list.h"

#include "communication/messages/outgoing_message.h"
#include "database/queries/moderation_query.h"

#include "shared.h"

/**
 * Load all fuserights.
 */
void fuserights_init() {
    global.fuserights_manager.fuserights = moderation_query_fuserights();
}

/**
 * Create fuserights struct.
 *
 * @param rank the minimum rank to check for
 * @param fuse_right the fuse right to check for
 * @return the new fuseright struct
 */
fuseright *fuserights_create(int rank, char *fuse_right) {
    fuseright *entry = malloc(sizeof(fuseright));
    entry->min_rank = rank;
    entry->fuse_right = strdup(fuse_right);
    return entry;
}

/**
 * Append avaliable fuserights to the client
 * @param rank the rank the user has
 * @param om the packet to append the fuserights to
 */
void fuserights_append(int rank, outgoing_message *om) {
    for (size_t i = 0; i < list_size(global.fuserights_manager.fuserights); i++) {
        fuseright *right;
        list_get_at(global.fuserights_manager.fuserights, i, (void *) &right);

        if (rank >= right->min_rank) {
            om_write_str(om, right->fuse_right);
        }
    }
}

/**
 * Returns if the rank has permission to use that fuseright
 * @param rank the rank to check
 * @param fuse the fuse to check
 * @return true, if successful
 */
bool fuserights_has_permission(int rank, char *fuse) {
    if (fuse == NULL) {
        return false;
    }

    for (size_t i = 0; i < list_size(global.fuserights_manager.fuserights); i++) {
        fuseright *right;
        list_get_at(global.fuserights_manager.fuserights, i, (void *) &right);

        if (rank >= right->min_rank && strcmp(right->fuse_right, fuse) == 0) {
            return true;
        }
    }

    return false;
}