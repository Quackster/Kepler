#ifndef INCOMING_MESSAGE_H
#define INCOMING_MESSAGE_H

#include "shared.h"

typedef struct incoming_message_s {
    char *header;
    char *data;
    int counter;
    int header_id;
    int total_length;
} incoming_message;

incoming_message *im_create(char*);
char *im_read_b64(incoming_message*);
int im_read_b64_int(incoming_message*);
int im_read_vl64(incoming_message*);
char *im_get_content(incoming_message*);
char *im_read_str(incoming_message *);
void *im_read(incoming_message *, int);
void im_cleanup(incoming_message*);

#endif