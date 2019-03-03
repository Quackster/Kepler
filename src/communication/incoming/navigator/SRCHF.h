#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void SRCHF(entity *player, incoming_message *message) {
    char *search_query = im_get_content(message);

    if (search_query == NULL) {
        return;
    }

    outgoing_message *om;
    List *searched_rooms = room_query_search(search_query);

    if (list_size(searched_rooms) > 0) {
        om = om_create(55); // "@w"

        for (size_t i = 0; i < list_size(searched_rooms); i++) {
            room *room;
            list_get_at(searched_rooms, i, (void *) &room);

            om_write_int_delimeter(om, room->room_id, 9);
            om_write_str_delimeter(om, room->room_data->name, 9);
            om_write_str_delimeter(om, room->room_data->owner_name, 9);

            if (room->room_data->accesstype == 2) {
                om_write_str_delimeter(om, "password", 9);
            }

            if (room->room_data->accesstype == 1) {
                om_write_str_delimeter(om, "closed", 9);
            }

            if (room->room_data->accesstype == 0) {
                om_write_str_delimeter(om, "open", 9);
            }

            om_write_str_delimeter(om, "x", 9);
            om_write_int_delimeter(om, room->room_data->visitors_now, 9);
            om_write_int_delimeter(om, room->room_data->visitors_max, 9);
            om_write_str_delimeter(om, "null", 9);
            om_write_str_delimeter(om, room->room_data->description, 9);
            om_write_str_delimeter(om, room->room_data->description, 9);
            om_write_char(om, 13);
        }

    } else {
        om = om_create(58); // "@z"
    }

    player_send(player, om);
    om_cleanup(om);

    // Clear room favourites if they weren't already loaded prior in the server
    for (size_t i = 0; i < list_size(searched_rooms); i++) {
        room *instance;
        list_get_at(searched_rooms, i, (void *) &instance);

        if (room_manager_get_by_id(instance->room_id) == NULL) {
            room_dispose(instance, false);
        }
    }

    cleanup:
    free(search_query);
}
