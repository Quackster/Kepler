#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#include "shared.h"

#include "sqlite3.h"
#include "list.h"
#include "hashtable.h"

#include "game/catalogue/catalogue_manager.h"
#include "game/catalogue/catalogue_page.h"
#include "game/catalogue/catalogue_item.h"
#include "game/catalogue/catalogue_package.h"

#include "database/queries/catalogue_query.h"
#include "database/db_connection.h"

/**
 *
 */
void catalogue_query_pages() {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT id, min_role, name_index, name, layout, image_headline, image_teasers, body, label_pick, label_extra_s, label_extra_t FROM catalogue_pages", -1, &stmt, 0);

    db_check_prepare(status, conn);

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        catalogue_page *page = catalogue_page_create(
            sqlite3_column_int(stmt, 0),
            sqlite3_column_int(stmt, 1),
            strdup((char*)sqlite3_column_text(stmt, 2)),
            strdup((char*)sqlite3_column_text(stmt, 3)),
            strdup((char*)sqlite3_column_text(stmt, 4)),
            strdup((char*)sqlite3_column_text(stmt, 5)),
            strdup((char*)sqlite3_column_text(stmt, 6)),
            strdup((char*)sqlite3_column_text(stmt, 7)),
            sqlite3_column_text(stmt, 8) != NULL ? strdup((char*)sqlite3_column_text(stmt, 8)) : NULL,
            sqlite3_column_text(stmt, 9) != NULL ? strdup((char*)sqlite3_column_text(stmt, 9)) : NULL,
            sqlite3_column_text(stmt, 10) != NULL ? strdup((char*)sqlite3_column_text(stmt, 10)) : NULL
        );

        catalogue_manager_add_page(page);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 *
 */
void catalogue_query_items() {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT * FROM catalogue_items", -1, &stmt, 0);

    db_check_prepare(status, conn);

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        catalogue_item *item = catalogue_item_create(
                (char *) sqlite3_column_text(stmt, 0),
                sqlite3_column_int(stmt, 1),
                sqlite3_column_int(stmt, 2),
                sqlite3_column_int(stmt, 3),
                sqlite3_column_int(stmt, 4),
                sqlite3_column_int(stmt, 5),
                (char *) sqlite3_column_text(stmt, 6),
                (char *) sqlite3_column_text(stmt, 7),
                (bool)sqlite3_column_int(stmt, 8)
        );

        catalogue_manager_add_item(item);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

void catalogue_query_packages() {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT * FROM catalogue_packages", -1, &stmt, 0);

    db_check_prepare(status, conn);

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        catalogue_package *package = catalogue_package_create(
                strdup((char *) sqlite3_column_text(stmt, 0)),
                sqlite3_column_int(stmt, 1),
                sqlite3_column_int(stmt, 2),
                sqlite3_column_int(stmt, 3)
        );

        catalogue_manager_add_package(package);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}
