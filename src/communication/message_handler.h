#ifndef MESSAGE_HANDLER_H
#define MESSAGE_HANDLER_H

#define MESSAGES 9999

typedef struct incoming_message_s incoming_message;
typedef struct entity_s entity;

typedef void (*mh_request)(entity*, incoming_message*);
mh_request message_requests[MESSAGES];

void message_handler_invoke(incoming_message *, entity *);
void message_handler_init();

#endif