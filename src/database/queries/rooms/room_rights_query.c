#include "room_rights_query.h"
#include "list.h"

#include "sqlite3.h"
#include "room_query.h"
#include "game/room/room.h"

#include "database/db_connection.h"
#include "shared.h"

/*
 * void room_query_add_rights(int room_id, int player_id);
void room_query_remove_rights(int room_id, int player_id);
List *room_query_rights(int room_id);
 */

/**
 * Add a room as a favourite.
 *
 * @param room_id the room id to add a favourite for
 * @param player_id the user id of the player that selected the room as their favourite
 */
void room_query_add_rights(int room_id, int player_id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "INSERT INTO rooms_rights (user_id,room_id) VALUES (?,?)", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, player_id);
        sqlite3_bind_int(stmt, 2, room_id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Remove a room favourite by given user id and room id.
 *
 * @param player_id the favourite to remove the room id for
 * @param room_id the room id to remove
 */
void room_query_remove_rights(int room_id, int player_id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "DELETE FROM rooms_rights WHERE user_id = ? AND room_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, player_id);
        sqlite3_bind_int(stmt, 2, room_id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Get list of room favourites by player id.
 *
 * @param player_id the player id to get the favourites for
 * @return the list of rooms
 */
List *room_query_rights(int room_id) {
    List *rights;
    list_new(&rights);

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT user_id FROM rooms_rights WHERE room_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, room_id);
    }

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        rights_entry *entry = rights_entry_create(sqlite3_column_int(stmt, 0));
        list_add(rights, entry);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return rights;
}
