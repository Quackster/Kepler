#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#include "list.h"
#include "shared.h"
#include "sqlite3.h"

#include "database/db_connection.h"
#include "item_query.h"

#include "game/items/item.h"
#include "game/items/definition/item_definition.h"

#include "game/pathfinder/coord.h"

/**
 * Get list of items in a users inventory.
 *
 * @param user_id the user id to get the list of items for
 * @return the list of items
 */
List *item_query_get_inventory(int user_id) {
    List *items;
    list_new(&items);

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT id,room_id,definition_id,x,y,z,wall_position,rotation,custom_data FROM items WHERE user_id = ? AND room_id = 0", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, user_id);
    }

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        item *item = item_create(
            sqlite3_column_int(stmt, 0),
            sqlite3_column_int(stmt, 1),
            user_id,
            sqlite3_column_int(stmt, 2),
            sqlite3_column_int(stmt, 3),
            sqlite3_column_int(stmt, 4),
            sqlite3_column_double(stmt, 5),
            strdup((char *) sqlite3_column_text(stmt, 6)),
            sqlite3_column_int(stmt, 7),
            strdup((char *) sqlite3_column_text(stmt, 8))
        );

        list_add(items, item);
    }

     db_check_finalize(sqlite3_finalize(stmt), conn);

    return items;
}

/**
 * Get list of items in a room by room id.
 *
 * @param room_id the room id to get the item list for
 * @return the list of items
 */
List *item_query_get_room_items(int room_id) {
    List *items;
    list_new(&items);

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT id,room_id,user_id,definition_id,x,y,z,wall_position,rotation,custom_data FROM items WHERE room_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, room_id);
    }

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        item *item = item_create(
                sqlite3_column_int(stmt, 0),
                sqlite3_column_int(stmt, 1),
                sqlite3_column_int(stmt, 2),
                sqlite3_column_int(stmt, 3),
                sqlite3_column_int(stmt, 4),
                sqlite3_column_int(stmt, 5),
                sqlite3_column_double(stmt, 6),
                strdup((char *) sqlite3_column_text(stmt, 7)),
                sqlite3_column_int(stmt, 8),
                strdup((char *) sqlite3_column_text(stmt, 9))
        );

        list_add(items, item);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return items;
}

/**
 * Creates a new item row in the database.
 *
 * @param user_id the owner of the item
 * @param room_id the room id the item is in, 0 for when it belongs in inventory
 * @param definition_id the definition id row entry for this item
 * @param x the x coordinate, 0 for none
 * @param y the y coordinate, 0 for none
 * @param z the z coordinate, 0 for none
 * @param rotation the item rotation, 0 for none
 * @param custom_data the custom data, for trophies, stickies, etc.
 * @return
 */
int item_query_create(int user_id, int room_id, int definition_id, int x, int y, double z, int rotation, char *custom_data) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int item_id = -1;
    int status = sqlite3_prepare_v2(conn, "INSERT INTO items (user_id, room_id, definition_id, x, y, z, rotation, custom_data, wall_position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, '')", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, user_id);
        sqlite3_bind_int(stmt, 2, room_id);
        sqlite3_bind_int(stmt, 3, definition_id);
        sqlite3_bind_int(stmt, 4, x);
        sqlite3_bind_int(stmt, 5, y);
        sqlite3_bind_double(stmt, 6, z);
        sqlite3_bind_int(stmt, 7, rotation);

        if (custom_data != NULL) {
            sqlite3_bind_text(stmt, 8, custom_data, (int) strlen(custom_data), SQLITE_STATIC);
        } else {
            sqlite3_bind_text(stmt, 8, "", 0, SQLITE_STATIC);
        }

        status = db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    if (status == SQLITE_DONE) {
        item_id = (int)sqlite3_last_insert_rowid(conn);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return item_id;
}

/**
 * Save the item details, will save everything, except the definition id.
 *
 * @param item the item instance to save
 */
void item_query_save(item *item) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE items SET room_id = ?, user_id = ?, x = ?, y = ?, z = ?, rotation = ?, custom_data = ?, wall_position = ? WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, item->room_id);
        sqlite3_bind_int(stmt, 2, item->owner_id);
        sqlite3_bind_int(stmt, 3, item->position->x);
        sqlite3_bind_int(stmt, 4, item->position->y);
        sqlite3_bind_double(stmt, 5, item->position->z);
        sqlite3_bind_int(stmt, 6, item->position->rotation);
        sqlite3_bind_text(stmt, 7, item->custom_data, (int) strlen(item->custom_data), SQLITE_STATIC);

        if (item->wall_position != NULL) {
            sqlite3_bind_text(stmt, 8, item->wall_position, (int) strlen(item->wall_position), SQLITE_STATIC);
        } else {
            sqlite3_bind_text(stmt, 8, "", (int) strlen(""), SQLITE_STATIC);
        }

        sqlite3_bind_int(stmt, 9, item->id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Delete the item from the database.
 *
 * @param item_id the item id to delete
 */
void item_query_delete(int item_id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "DELETE FROM items WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, item_id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}
