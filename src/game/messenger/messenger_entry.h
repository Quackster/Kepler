#ifndef MESSENGER_FRIEND_H
#define MESSENGER_FRIEND_H

typedef struct outgoing_message_s outgoing_message;

typedef struct messenger_entry_s {
    int user_id;
} messenger_entry;

messenger_entry *messenger_entry_create(int friend_id);
void messenger_entry_cleanup(messenger_entry*);
void messenger_entry_serialise(int, outgoing_message*);

#endif