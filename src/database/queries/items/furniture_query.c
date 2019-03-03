#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#include "hashtable.h"
#include "shared.h"
#include "sqlite3.h"

#include "furniture_query.h"
#include "database/db_connection.h"

#include "game/items/definition/item_definition.h"

/**
 * Get all the furniture definitions from database.
 *
 * @return the hashtable of furniture definitions
 */
HashTable *furniture_query_definitions() {
    HashTable *furniture;
    hashtable_new(&furniture);

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT * FROM items_definitions", -1, &stmt, 0);
    db_check_prepare(status, conn);

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        item_definition *def = item_definition_create(
                sqlite3_column_int(stmt, 0),
                sqlite3_column_int(stmt, 1),
                (char *) sqlite3_column_text(stmt, 2),
                (char *) sqlite3_column_text(stmt, 3),
                sqlite3_column_int(stmt, 4),
                sqlite3_column_int(stmt, 5),
                sqlite3_column_double(stmt, 6),
                (char *) sqlite3_column_text(stmt, 7)
        );

        hashtable_add(furniture, &def->id, def);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
    return furniture;
}