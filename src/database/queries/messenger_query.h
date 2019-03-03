#ifndef MESSENGER_QUERY_H
#define MESSENGER_QUERY_H

typedef struct list_s List;

List *messenger_query_get_friends(int);
List *messenger_query_get_requests(int);
int messenger_query_new_request(int, int);
int messenger_query_new_friend(int, int);
int messenger_query_delete_request(int, int);
int messenger_query_delete_friend(int, int);
int messenger_query_request_exists(int, int);
int messenger_query_new_message(int, int, char*, char*);
List *messenger_query_unread_messages(int);
void messenger_query_mark_read(int);

#endif