#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/room/room.h"

#include "util/stringbuilder.h"
#include "database/queries/rooms/room_favourites_query.h"

void GETFVRF(entity *player, incoming_message *message) {
    List *favourite_rooms = room_query_favourites(player->details->id);

    outgoing_message *om = om_create(61); // "@}"
    sb_add_string(om->sb, "HHJ\2HHH");
    om_write_int(om, (int) list_size(favourite_rooms));

    for (size_t i = 0; i < list_size(favourite_rooms); i++) {
        room *room;
        list_get_at(favourite_rooms, i, (void *) &room);
        room_append_data(room, om, player);
    }

    player_send(player, om);
    om_cleanup(om);

    // Clear room favourites if they weren't already loaded prior in the server
    if (favourite_rooms != NULL) {
        for (size_t i = 0; i < list_size(favourite_rooms); i++) {
            room *instance;
            list_get_at(favourite_rooms, i, (void *) &instance);

            if (room_manager_get_by_id(instance->room_id) == NULL) {
                room_dispose(instance, false);
            }
        }
    }

    // Destroy list of favourite rooms
    list_destroy(favourite_rooms);
}