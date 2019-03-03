#ifndef MESSENGER_GETMESSAGES_H
#define MESSENGER_GETMESSAGES_H

#include "list.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void MESSENGER_GETMESSAGES(entity *p, incoming_message *message) {
    for (size_t i = 0; i < list_size(p->messenger->messages); i++) {
        messenger_message *messenger_msg;
        list_get_at(p->messenger->messages, i, (void*)&messenger_msg);

        outgoing_message *response = om_create(134); // "BF"
        //om_write_int(response, 1);
        om_write_int(response, messenger_msg->id);
        om_write_int(response, messenger_msg->receiver_id);
        om_write_str(response, messenger_msg->date);
        om_write_str(response, messenger_msg->body);
        player_send(p, response);
        om_cleanup(response);
    }
}

#endif