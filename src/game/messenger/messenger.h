#ifndef MESSENGER_H
#define MESSENGER_H

typedef struct list_s List;
typedef struct entity_s entity;

typedef struct messenger_s {
    List *friends;
    List *requests;
    List *messages;
} messenger;

typedef struct messenger_message_s {
    int id;
    int receiver_id;
    int sender_id;
    char *body;
    char *date;
} messenger_message;

messenger *messenger_create();
messenger_message *messenger_message_create(int, int, int, char*, char*);
void messenger_init(entity*);
int messenger_is_friends(messenger*, int);
int messenger_has_request(messenger*, int);
void messenger_remove_request(messenger*, int);
void messenger_remove_friend(messenger*, int);

void messenger_cleanup_list(List*);
void messenger_dispose(messenger *);

#endif