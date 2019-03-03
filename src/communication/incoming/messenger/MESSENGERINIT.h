#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/messenger/messenger.h"
#include "game/messenger/messenger_entry.h"

#include "communication/incoming/messenger/MESSENGER_GETREQUESTS.h"
#include "communication/incoming/messenger/MESSENGER_GETMESSAGES.h"

#include "list.h"

void MESSENGERINIT(entity *p, incoming_message *message) {
    if (p->details == NULL) {
        return;
    }
    
    outgoing_message *friends_list = om_create(12); // "@L"
    om_write_str(friends_list, p->details->console_motto);
    om_write_int(friends_list, 600);
    om_write_int(friends_list, 200);
    om_write_int(friends_list, 600);
    om_write_int(friends_list, (int) list_size(p->messenger->friends)); // Buddy list count

    for (size_t i = 0; i < list_size(p->messenger->friends); i++) {
        messenger_entry *friend;
        list_get_at(p->messenger->friends, i, (void*)&friend);

        messenger_entry_serialise(friend->user_id, friends_list);
    }

    player_send(p, friends_list);
    om_cleanup(friends_list);

    /* V14 messenger sends message 191 and message 233 after receiving @L, V13 does not.
    Invoke it manually... */

    MESSENGER_GETREQUESTS(p, message);
    MESSENGER_GETMESSAGES(p, message);
}
