#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"
#include "time.h"

void GDATE(entity *player, incoming_message *message) {
    time_t t = time(NULL);
    struct tm tm = *localtime(&t);

    char date[11];
    sprintf(date, "%02d-%02d-%d", tm.tm_mday, tm.tm_mon + 1, tm.tm_year + 1900);
    
    outgoing_message *om = om_create(163); // "Bc"
    om_write_str(om, date);
    player_send(player, om);
    om_cleanup(om);
}