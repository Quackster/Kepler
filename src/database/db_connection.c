#include <stdbool.h>

#include "sqlite3.h"
#include "main.h"
#include "shared.h"
#include "log.h"

#include "db_connection.h"
#include "util/configuration/configuration.h"


bool db_initialise() {
    if (!sqlite3_threadsafe()) {
        log_info("SQLite not threadsafe");
        return false;
    } else {
        if (sqlite3_config(SQLITE_CONFIG_SERIALIZED) != SQLITE_OK) {
            log_fatal("Could not configure SQLite to use serialized mode");
            return false;
        }
    }

    log_info("Testing SQLite connection...");

    sqlite3 *con = db_create_connection();

    if (con == NULL) {
        log_info("The database connection was unsuccessful, program aborted!");
        sqlite3_close(con);
        return false;
    }

    log_info("The database connection was successful!");

    sqlite3_stmt *stmt;
    int status = sqlite3_prepare_v2(con, "PRAGMA journal_mode=WAL;", -1, &stmt, 0);

    db_check_prepare(status, con);
    db_check_step(sqlite3_step(stmt), con, stmt);

    char *chosen_journal_mode = (char *) sqlite3_column_text(stmt, 0);

    if (strcmp(chosen_journal_mode, "wal") != 0) {
        log_warn("WAL not supported, now using: %s", chosen_journal_mode);
    }

    db_check_finalize(sqlite3_finalize(stmt), con);

    global.DB = con;
    global.is_shutdown = false;

    return true;
}

/**
 * Create a MySQL connection instance and select the database, will print errors if the connection
 * was not successful.
 *
 * @return the MYSQL connection
 */
sqlite3 *db_create_connection() {
    FILE *file = NULL;
    char *err_msg = NULL;

    bool run_query = false;

    if (!(file = fopen(configuration_get_string("database.filename"), "r"))) {
        log_warn("Database does not exist, creating...");
        run_query = true;
    }

    sqlite3 *db;

    // Open database in read/write mode, create if not exists and use serialized mode
    // In serialized mode (FULLMUTEX) a connection can be shared across N threads
    // without having to worry about any synchronization or locking
    int rc = sqlite3_open_v2(configuration_get_string("database.filename"), &db, SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE | SQLITE_OPEN_FULLMUTEX, NULL);

    if (rc != SQLITE_OK) {
        log_fatal("Cannot open database: %s", sqlite3_errmsg(db));
        sqlite3_close(db);
        return NULL;
    } else {
        if (run_query) {
            log_info("Executing queries...");

            char *buffer = db_load_sql_file();
            rc = sqlite3_exec(db, buffer, 0, 0, &err_msg);

            if (rc != SQLITE_OK ) {
                log_fatal("SQL error: %s", err_msg);
                sqlite3_free(err_msg);
                sqlite3_close(db);
            }

            free(buffer);
        }
    }

    if (file != NULL) {
        fclose(file);
    }

    // The CMS might be locking the database file for a small period of legitimate
    // Therefore we define a timeout of 300ms
    // 300ms to handle slow mediums like NTFS on a spinning 5400rpm disk
    sqlite3_busy_timeout(db, 300);

    return db;
}

/**
 * Loads the .sql file from disk to create a new db, must be manually freed.
 *
 * @param path the file to load
 * @return the file contents
 */
char* db_load_sql_file() {
    char path[] = "kepler.sql";
    char* buffer = NULL;
    size_t length;
    FILE * f = fopen (path, "rb"); //was "rb"

    if (f) {
        fseek (f, 0, SEEK_END);
        length = (size_t )ftell (f);
        fseek (f, 0, SEEK_SET);
        buffer = (char*)malloc ((length+1)*sizeof(char));

        if (buffer) {
            if (!fread (buffer, sizeof(char), length, f)) {
                return NULL;
            }
        }

        fclose (f);

        if (buffer != NULL) {
            buffer[length] = '\0';
        }
    }

    return buffer;
}

/**
 * Execute a string given to the database
 *
 * @param query the query to execute
 */
int db_execute_query(char *query) {
    char *err_msg;
    int rc = sqlite3_exec(global.DB, query, 0, 0, &err_msg);

    if (rc != SQLITE_OK ) {
        log_fatal("SQL error: %s", err_msg);
        sqlite3_free(err_msg);
        return -1;
    };

    return sqlite3_changes(global.DB);
}

/**
 * Check return status of prepare, log if not okay
 *
 * @param status return value of sqlite3_prepare_v2
 * @param conn SQLite3 connection
 */
int db_check_prepare(int status, sqlite3 *conn) {
    if (status != SQLITE_OK) {
        log_fatal("Failed to prepare statement: %s", sqlite3_errmsg(conn));

        // Cleanup
        sqlite3_close(conn);
        dispose_program();

        // We exit on error to keep ACID consistency
        exit(EXIT_FAILURE);
    }
    return status;
}

/**
 * Check return status of finalize, log if not okay
 *
 * @param status return value of sqlite3_finalize
 * @param conn SQLite3 connection
 */
int db_check_finalize(int status, sqlite3 *conn) {
    if (status != SQLITE_OK) {
        log_fatal("Could not finalize (cleanup) stmt. %s", sqlite3_errmsg(conn));

        // Cleanup
        sqlite3_close(conn);
        dispose_program();

        // We exit on error to keep ACID consistency
        exit(EXIT_FAILURE);
    }
    return status;
}

/**
 * Check return status of step, log if not okay
 *
 * @param status return value of sqlite3_step
 * @param conn SQLite3 connection
 * @param stmt SQLite3 statement
 */
int db_check_step(int status, sqlite3 *conn, sqlite3_stmt *stmt) {
    if (status != SQLITE_DONE && status != SQLITE_ROW) {
        log_fatal("Could not step (execute) stmt. %s", sqlite3_errmsg(conn));

        // Cleanup
        sqlite3_finalize(stmt);
        sqlite3_close(conn);
        dispose_program();

        // We exit on error to keep ACID consistency
        exit(EXIT_FAILURE);
    }
    return status;
}