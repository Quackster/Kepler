#ifndef OUTGOING_MESSAGE_H
#define OUTGOING_MESSAGE_H

typedef struct entity_s entity;
typedef struct stringbuilder_s stringbuilder;

typedef struct outgoing_message_s {
    int header_id;
    char *header;
    int finalised;
    stringbuilder *sb;
} outgoing_message;

outgoing_message *om_create(int);
void om_write_str(outgoing_message*, char*);
void om_write_str_kv(outgoing_message*, char*, char*);
void om_write_str_delimeter(outgoing_message*, char*, int);
void om_write_int_delimeter(outgoing_message*, int, int);
void om_write_char(outgoing_message *, int);
void om_write_str_int(outgoing_message*, int);
void om_write_int(outgoing_message*, int);
void om_finalise(outgoing_message*);
void om_cleanup(outgoing_message*);

#endif