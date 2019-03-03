#include "list.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void MESSENGER_MARKREAD(entity *p, incoming_message *message) {
    int message_id = im_read_vl64(message);
    messenger_query_mark_read(message_id);
}
