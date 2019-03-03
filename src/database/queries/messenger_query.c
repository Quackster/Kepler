#include <stdio.h>
#include <stdbool.h>

#include "database/db_connection.h"
#include "database/queries/messenger_query.h"

#include "game/messenger/messenger.h"
#include "game/messenger/messenger_entry.h"

#include "sqlite3.h"
#include "shared.h"
#include "list.h"
#include "log.h"

/**
 * Get friends by user id
 * @param user_id the user id
 * @return
 */
List *messenger_query_get_friends(int user_id) {
    List *friends;
    list_new(&friends);

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT to_id, from_id FROM messenger_friends WHERE to_id = ? OR from_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, user_id);
        sqlite3_bind_int(stmt, 2, user_id);
    }

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        int to_id = sqlite3_column_int(stmt, 0);
        int from_id = sqlite3_column_int(stmt, 1);

        int friend_id = -1;

        if (to_id != user_id) {
            friend_id = to_id;
        } else {
            friend_id = from_id;
        }

        messenger_entry *friend = messenger_entry_create(friend_id);
        list_add(friends, (void*)friend);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return friends;
}

/**
 * Get requests towards the user id
 *
 * @param user_id the user id
 * @return
 */
List *messenger_query_get_requests(int user_id) {
    List *requests;
    list_new(&requests);

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT from_id FROM messenger_requests WHERE to_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, user_id);
    }

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        messenger_entry *friend = messenger_entry_create(sqlite3_column_int(stmt, 0));
        list_add(requests, (void*)friend);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return requests;
}

/**
 * Insert a new request into the database
 *
 * @param to_id the id that the request is sent from
 * @param from_id the id that the request is sent to
 */
int messenger_query_new_request(int from_id, int to_id) {
    if (messenger_query_request_exists(from_id, to_id)) {
        log_debug("debug 3");
        return 0;
    }

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "INSERT INTO messenger_requests (from_id, to_id) VALUES (?, ?)", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, from_id);
        sqlite3_bind_int(stmt, 2, to_id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return 1;
}

/**
 * Insert a new friend into the database
 *
 * @param to_id the id that the request is sent from
 * @param from_id the id that the request is sent to
 */
int messenger_query_new_friend(int from_id, int to_id) {
    if (!messenger_query_request_exists(from_id, to_id)) {
        return 0;
    }

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "INSERT INTO messenger_friends (from_id, to_id) VALUES (?, ?)", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, from_id);
        sqlite3_bind_int(stmt, 2, to_id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return 1;
}

/**
 * Deletes a request from the database
 *
 * @param to_id the id that the request is sent from
 * @param from_id the id that the request is sent to
 */
int messenger_query_delete_request(int from_id, int to_id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "DELETE FROM messenger_requests WHERE from_id = ? AND to_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, from_id);
        sqlite3_bind_int(stmt, 2, to_id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return 1;
}

/**
 * Deletes a friend from the database
 *
 * @param to_id the id that the request is sent from
 * @param from_id the id that the request is sent to
 */
int messenger_query_delete_friend(int from_id, int to_id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "DELETE FROM messenger_friends WHERE from_id = ? AND to_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, from_id);
        sqlite3_bind_int(stmt, 2, to_id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return 1;
}

/**
 * Insert a new request into the database
 *
 * @param to_id the id that the request is sent from
 * @param from_id the id that the request is sent to
 */
int messenger_query_request_exists(int from_id, int to_id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int result = 0;
    int status = sqlite3_prepare_v2(conn, "SELECT * FROM messenger_requests WHERE to_id = ? AND from_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, to_id);
        sqlite3_bind_int(stmt, 2, from_id);
    }

    int step = db_check_step(sqlite3_step(stmt), conn, stmt);

    if (step == SQLITE_ROW) {
        result = 1;
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return result;
}

/**
 * Insert a new message into the database
 *
 * @param to_id the id that the request is sent from
 * @param from_id the id that the request is sent to
 */
int messenger_query_new_message(int receiver_id, int sender_id, char *body, char *date) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "INSERT INTO messenger_messages (receiver_id, sender_id, unread, body, date) VALUES (?, ?, ?, ?, ?)", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, receiver_id);
        sqlite3_bind_int(stmt, 2, sender_id);
        sqlite3_bind_int(stmt, 3, 1);
        sqlite3_bind_text(stmt, 4, body, -1, SQLITE_STATIC);
        sqlite3_bind_text(stmt, 5, date, -1, SQLITE_STATIC);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    int row_id = (int) sqlite3_last_insert_rowid(conn);

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return row_id;
}

/**
 * Load all unread messages for user.
 *
 * @param user_id the id to load the messages for
 * @return list of unread messages
 */
List *messenger_query_unread_messages(int user_id) {
    List *messages;
    list_new(&messages);

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT id,receiver_id,sender_id,body,date FROM messenger_messages WHERE receiver_id = ? AND unread = 1", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, user_id);
    }

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        int id = sqlite3_column_int(stmt, 0);
        int receiver_id = sqlite3_column_int(stmt, 1);
        int sender_id = sqlite3_column_int(stmt, 2);
        char *body = (char*)sqlite3_column_text(stmt, 3);
        char *date = (char*)sqlite3_column_text(stmt, 4);

        messenger_message *msg = messenger_message_create(id, receiver_id, sender_id, body, date);
        list_add(messages, msg);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return messages;
}

/**
 * Mark message as read
 */
void messenger_query_mark_read(int message_id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE messenger_messages SET unread = 0 WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, message_id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}