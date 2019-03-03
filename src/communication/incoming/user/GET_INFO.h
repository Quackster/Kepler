#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void GET_INFO(entity *player, incoming_message *message) {
    outgoing_message *user_info = om_create(5);
    om_write_str_int(user_info, player->details->id);
    om_write_str(user_info, player->details->username);
    om_write_str(user_info, player->details->figure);
    om_write_str(user_info, player->details->sex);
    om_write_str(user_info, player->details->motto);
    om_write_int(user_info, player->details->tickets);
    om_write_str(user_info, player->details->pool_figure); // pool figure
    om_write_int(user_info, player->details->film);
    player_send(player, user_info);
    om_cleanup(user_info);

}
