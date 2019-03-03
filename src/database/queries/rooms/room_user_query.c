#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#include "sqlite3.h"
#include "shared.h"

#include "game/room/room.h"
#include "room_user_query.h"

#include "database/db_connection.h"

int room_query_create(int owner_id, char *room_name, char *room_model, char *room_show_name) {
    char *room_description = "";
    int showname = 1;
    int room_id = -1;

    if (strcmp(room_show_name, "0") == 0) {
        showname = 0;
    }

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "INSERT INTO rooms (owner_id, name, description, model, showname, password) VALUES (?,?,?,?,?, '')", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, owner_id);
        sqlite3_bind_text(stmt, 2, room_name, (int)strlen(room_name), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 3, room_description, (int)strlen(room_description), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 4, room_model, (int)strlen(room_model), SQLITE_STATIC);
        sqlite3_bind_int(stmt, 5, showname);

        status = db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    if (status == SQLITE_DONE) {
        room_id = (int)sqlite3_last_insert_rowid(conn);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return room_id;
}