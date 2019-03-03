#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void G_USRS(entity *p, incoming_message *message) {
    if (p->room_user->room == NULL) {
        return;
    }

    if (p->room_user->room->room_data->model_data == NULL) {
        log_fatal("Room %i has invalid model data.", p->room_user->room->room_data->id);
        return;
    }

    room *room = p->room_user->room;

    /*ListIter iter;
    list_iter_init(&iter, rooms->users);

    outgoing_message *players = om_create(28); // "@\"

    entity *user;
    while (list_iter_next(&iter, (void*) &user) != CC_ITER_END) {
        append_user_list(players, user);
    }

    player_send(p, players);
    om_cleanup(players);*/

    outgoing_message *players = om_create(28); // "@\"

    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *room_player;
        list_get_at(room->users, i, (void*)&room_player);
        append_user_list(players, room_player);
    }

    player_send(p, players);
    om_cleanup(players);

    players = om_create(28); // "@\"
    append_user_list(players, p);
    room_send(room, players);
    om_cleanup(players);

    /*char instance_id[11];
    sprintf(instance_id, "%i", entity->details->id);
    instance_id[10] = '\0';

    outgoing_message *players = om_create(28); // "@\"
    om_write_str_kv(players, "i", "0");
    om_write_str_kv(players, "a", instance_id);
    om_write_str_kv(players, "n", entity->details->username);
    om_write_str_kv(players, "f", entity->details->figure);
    sb_add_string(players->sb, "l");
    sb_add_string(players->sb, ":");
    sb_add_int(players->sb, entity->room_user->rooms->room_data->model_data->door_x);
    sb_add_int(players->sb, entity->room_user->rooms->room_data->model_data->door_y);
    sb_add_float(players->sb, entity->room_user->rooms->room_data->model_data->door_z);
    sb_add_char(players->sb, 13);
    om_write_str_kv(players, "c", entity->details->motto);
    player_send(entity, players);
    om_cleanup(players);*/
}