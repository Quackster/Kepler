#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdbool.h>
#include <sodium.h>

#include "sqlite3.h"
#include "shared.h"
#include "main.h"
#include "log.h"

#include "game/player/player.h"

#include "database/queries/player_query.h"
#include "database/db_connection.h"

/**
 *
 * @param user_id
 * @return
 */
char *player_query_username(int user_id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    char *username = NULL;
    int status = sqlite3_prepare_v2(conn, "SELECT username FROM users WHERE id = ? LIMIT 1", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, user_id);

        status = db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    if (status == SQLITE_ROW) {
        username = strdup((char*)sqlite3_column_text(stmt, 0));
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return username;
}

/**
 *
 * @param user_id
 * @return
 */
int player_query_id(char *username) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int USER_ID = -1;
    int status = sqlite3_prepare_v2(conn, "SELECT id FROM users WHERE username = ? LIMIT 1", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_text(stmt, 1, username, -1, SQLITE_STATIC);

        status = db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    if (status == SQLITE_ROW) {
        USER_ID = sqlite3_column_int(stmt, 0);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return USER_ID;
}

/**
 * Retrieves the user ID if there was a successful login.
 *
 * @param username the username
 * @param password the password
 * @return the user id, -1 if not successful
 */
int player_query_login(char *username, char *password) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int USER_ID = -1;
    int status = sqlite3_prepare_v2(conn, "SELECT id, password FROM users WHERE username = ? LIMIT 1", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_text(stmt, 1, username, -1, SQLITE_STATIC);

        status = db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    if (status == SQLITE_ROW) {
        USER_ID = sqlite3_column_int(stmt, 0);
        char *hashed_password = (char*)sqlite3_column_text(stmt, 1);

        // 0 is valid, -1 is unvalid
        int valid = crypto_pwhash_str_verify(hashed_password, password, strlen(password));

        if (valid == 0 && crypto_pwhash_str_needs_rehash(hashed_password, crypto_pwhash_OPSLIMIT_INTERACTIVE, crypto_pwhash_MEMLIMIT_INTERACTIVE)) {
            char hashed_password[crypto_pwhash_STRBYTES];

            // Hash password
            /*if (crypto_pwhash_str(hashed_password, password, strlen(password), crypto_pwhash_OPSLIMIT_INTERACTIVE, crypto_pwhash_MEMLIMIT_INTERACTIVE) != 0) {
                // Will only allocate 64MB, but just in case
                log_fatal("Not enough memory to hash passwords");
                exit_program();
                return -1;
            }*/

            // TODO: update password in database
        }

        // Clear password from memory securely
        sodium_memzero(password, strlen(password));

        // Wrong password
        if (valid == -1) {
            return -1;
        }
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return USER_ID;
}

/**
 * Retrieves the user ID if a SSO ticket matches
 *
 * @param ticket the SSO (Single Sign On) ticket
 * @return the user id, -1 if not successful
 */
int player_query_sso(char *ticket) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int SUCCESS = -1;
    int status = sqlite3_prepare_v2(conn, "SELECT id FROM users WHERE sso_ticket = ? LIMIT 1", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_text(stmt, 1, ticket, -1, SQLITE_STATIC);

        status = db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    if (status == SQLITE_ROW) {
        SUCCESS = sqlite3_column_int(stmt, 0);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return SUCCESS;
}

/**
 * Returns true or false if the username exists, required for registration.
 *
 * @param username the username
 * @return true, if successful
 */
int player_query_exists_username(char *username) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT id FROM users WHERE username = ? LIMIT 1", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_text(stmt, 1, username, -1, SQLITE_STATIC);

        status = db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return status == SQLITE_ROW; // row exists
}

/**
 * Get player data by user id, must be freed manually.
 *
 * @param id the user id
 * @return the player data struct
 */
entity_data *player_query_data(int id) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    entity_data *player_data = NULL;
    int status = sqlite3_prepare_v2(conn, "SELECT id,username,password,figure,pool_figure,credits,motto,sex,tickets,film,rank,console_motto,last_online,club_subscribed,club_expiration,badge,badge_active FROM users WHERE id = ? LIMIT 1", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, id);

        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            return NULL;
        }

        player_data = player_create_data(
                sqlite3_column_int(stmt, 0), // id
                (char*)sqlite3_column_text(stmt, 1),  // username
                (char*)sqlite3_column_text(stmt, 2),  // password
                (char*)sqlite3_column_text(stmt, 3),  // figure
                (char*)sqlite3_column_text(stmt, 4),  // pool_figure
                sqlite3_column_int(stmt, 5),          // credits
                (char*)sqlite3_column_text(stmt, 6),  // motto
                (char*)sqlite3_column_text(stmt, 7),  // sex
                sqlite3_column_int(stmt, 8),          // tickets
                sqlite3_column_int(stmt, 9),          // film
                sqlite3_column_int(stmt, 10),         // rank
                (char*)sqlite3_column_text(stmt, 11), // console_motto
                (unsigned long)sqlite3_column_int64(stmt, 12), // last_online
                (unsigned long)sqlite3_column_int64(stmt, 13), // club_subscribed
                (unsigned long)sqlite3_column_int64(stmt, 14), // club_expiration
                (char*)sqlite3_column_text(stmt, 15), // badge
                (bool)sqlite3_column_int(stmt, 16) == 1
        );
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return player_data;
}

/**
 * Creates a new player instance.
 *
 * @param username the username
 * @param figure the figure
 * @param gender the gender
 * @param password the password
 *
 * @return the inserted player id
 */
int player_query_create(char *username, char *figure, char *gender, char *password) {
    char hashed_password[crypto_pwhash_STRBYTES];

    // Hash password
    if (crypto_pwhash_str(hashed_password, password, strlen(password), crypto_pwhash_OPSLIMIT_INTERACTIVE, crypto_pwhash_MEMLIMIT_INTERACTIVE) != 0) {
        // Will only allocate 64MB, but just in case
        log_fatal("Not enough memory to hash passwords");
        exit_program();
        return -1;
    }

    // Clear password from memory securely
    sodium_memzero(password, strlen(password));

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "INSERT INTO users (username, password, sex, figure, pool_figure, last_online) VALUES (?,?,?,?,?,?)", -1, &stmt, 0);

    db_check_prepare(status, conn);

    char last_online[100];
    sprintf(last_online, "%lu", (unsigned long)time(NULL));

    if (status == SQLITE_OK) {
        sqlite3_bind_text(stmt, 1, username, (int) strlen(username), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 2, hashed_password, (int) strlen(hashed_password), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 3, gender, (int) strlen(gender), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 4, figure, (int) strlen(figure), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 5, "", (int) strlen(""), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 6, last_online, (int) strlen(last_online), SQLITE_STATIC);

        status = db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    int user_id = -1;

    if (status == SQLITE_OK) {
        user_id = (int)sqlite3_last_insert_rowid(conn);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    return user_id;
}

/**
 * Save looks for player.
 *
 * @param player the player to save the looks for
 */
void player_query_save_details(entity *player) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE users SET figure = ?, pool_figure = ?, sex = ?, rank = ? WHERE id = ?", -1, &stmt, 0);\

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_text(stmt, 1, player->details->figure, (int) strlen(player->details->figure), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 2, player->details->pool_figure, (int) strlen(player->details->pool_figure), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 3, player->details->sex, (int) strlen(player->details->sex), SQLITE_STATIC);
        sqlite3_bind_int(stmt, 4, player->details->rank);
        sqlite3_bind_int(stmt, 5, player->details->id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Save last online for player
 *
 * @param player the player to save the last online for
 */
void player_query_save_last_online(entity *player) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE users SET last_online = ? WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int64(stmt, 1, (sqlite3_int64)time(NULL));
        sqlite3_bind_int(stmt, 2, player->details->id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Save motto for player
 *
 * @param player the player to save the mottos for
 */
void player_query_save_motto(entity *player) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE users SET motto = ?, console_motto = ? WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_text(stmt, 1, player->details->motto, (int) strlen(player->details->motto), SQLITE_STATIC);
        sqlite3_bind_text(stmt, 2, player->details->console_motto, (int) strlen(player->details->console_motto), SQLITE_STATIC);
        sqlite3_bind_int(stmt, 3, player->details->id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Save currency for player
 *
 * @param player the player to save the currency amount for
 */
void player_query_save_currency(entity *player) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE users SET credits = ?, tickets = ?, film = ? WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, player->details->credits);
        sqlite3_bind_int(stmt, 2, player->details->tickets);
        sqlite3_bind_int(stmt, 3, player->details->film);
        sqlite3_bind_int(stmt, 4, player->details->id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Player tickets for player
 *
 * @param id the player id to save the tickets for
 * @param tickets the ticket amount to save
 */
void player_query_save_tickets(int id, int tickets) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE users SET tickets = ? WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int(stmt, 1, tickets);
        sqlite3_bind_int(stmt, 2, id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Get badges for player
 *
 * @param player the player to get the badges for
 */
Array *player_query_badges(int id) {
    Array *badges;
    if (array_new(&badges) != CC_OK) {
        log_fatal("Couldn't create array to hold badges in player_query_badges");
        return NULL;
    }

    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "SELECT badge FROM users_badges WHERE user_id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    sqlite3_bind_int(stmt, 1, id);

    while (true) {
        status = db_check_step(sqlite3_step(stmt), conn, stmt);

        if (status != SQLITE_ROW) {
            break;
        }

        char *badge = strdup((char*)sqlite3_column_text(stmt, 0));

        if (array_add(badges, badge) != CC_OK) {
            log_fatal("Couldn't add badge to badges in player_query_badges");
            return NULL;
        }
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);

    entity_data *player_data = player_query_data(id);

    if (player_data->rank >= 5) {
        if (array_add(badges, "ADM") != CC_OK) {
            log_fatal("Couldn't add ADM badge to badges in player_query_badges");
            return NULL;
        }
    }

    if (player_data->rank >= 2) {
        if (array_add(badges, "HC1") != CC_OK) {
            log_fatal("Couldn't add HC1 badge to badges in player_query_badges");
            return NULL;
        }
    }

    // TODO: HC2 badge

    player_data_cleanup(player_data);

    return badges;
}

/**
 * Save active badge to database
 *
 * @param player the player to save the active badge for
 */
void player_query_save_badge(entity *player) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE users SET badge = ?, badge_active = ? WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_text(stmt, 1, player->details->badge, (int) strlen(player->details->badge), SQLITE_STATIC);
        sqlite3_bind_int(stmt, 2, (int)player->details->badge_active);
        sqlite3_bind_int(stmt, 3, player->details->id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}

/**
 * Save club information for player
 *
 * @param player the player to save the club information for
 */
void player_query_save_club_information(entity *player) {
    sqlite3 *conn = global.DB;
    sqlite3_stmt *stmt;

    int status = sqlite3_prepare_v2(conn, "UPDATE users SET club_subscribed = ?,club_expiration = ? WHERE id = ?", -1, &stmt, 0);

    db_check_prepare(status, conn);

    if (status == SQLITE_OK) {
        sqlite3_bind_int64(stmt, 1, (sqlite3_int64)player->details->club_subscribed);
        sqlite3_bind_int64(stmt, 2, (sqlite3_int64)player->details->club_expiration);
        sqlite3_bind_int(stmt, 3, player->details->id);

        db_check_step(sqlite3_step(stmt), conn, stmt);
    }

    db_check_finalize(sqlite3_finalize(stmt), conn);
}