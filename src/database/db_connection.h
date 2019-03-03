#ifndef DB_CONNECTION_H
#define DB_CONNECTION_H

#include <stdbool.h>

typedef struct sqlite3 sqlite3;
typedef struct sqlite3_stmt sqlite3_stmt;

bool db_initialise();
sqlite3 *db_create_connection();
char* db_load_sql_file();
int db_execute_query(char *query);
int db_check_prepare(int status, sqlite3 *conn);
int db_check_finalize(int status, sqlite3 *conn);
int db_check_step(int status, sqlite3 *conn, sqlite3_stmt *stmt);

#endif