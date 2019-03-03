#include <stdio.h>

#include "list.h"

#include "shared.h"
#include "sqlite3.h"

#include "moderation_query.h"
#include "database/db_connection.h"

#include "game/moderation/fuserights_manager.h"

/**
 * Get all the fuserights from the database
 *
 * @return the listt of fuserights
 */
List *moderation_query_fuserights() {
    List *fuserights;
    list_new(&fuserights);

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT * FROM rank_fuserights", -1, &stmt, 0);
    db_check_prepare(status, conn);

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        fuseright *entry = fuserights_create(
                sqlite3_column_int(stmt, 0),
                (char *) sqlite3_column_text(stmt, 1)
        );

        list_add(fuserights, entry);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
    return fuserights;
}