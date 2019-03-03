#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/navigator/navigator_category.h"
#include "game/navigator/navigator_category_manager.h"

#include "game/room/room.h"

#include "list.h"

#include "database/queries/rooms/room_query.h"

void NAVIGATE(entity *player, incoming_message *message) {
    int hide_full = im_read_vl64(message);
    int category_id = im_read_vl64(message);

    room_category *parent_category = category_manager_get_by_id(category_id);
    outgoing_message *navigator = om_create(220); // "C\"

    if (parent_category != NULL && category_has_access(parent_category, player->details->rank)) {
        om_write_int(navigator, hide_full);
        om_write_int(navigator, category_id);

        if (parent_category->category_type == PUBLIC) {
            om_write_int(navigator, 0);
        } else {
            om_write_int(navigator, 2);
        }

        om_write_str(navigator, parent_category->name);
        om_write_int(navigator, category_manager_get_current_vistors(category_id)); // current visitors
        om_write_int(navigator, category_manager_get_max_vistors(category_id)); // max visitorss
        om_write_int(navigator, parent_category->parent_id); 

        List *rooms = category_manager_get_rooms(parent_category->id);
        List *recent_rooms = NULL;

        if (parent_category->category_type == PRIVATE) {
            recent_rooms = room_query_recent_rooms(10, parent_category->id);

            // Remove full rooms if hide full
            for (size_t i = 0; i < list_size(rooms); i++) {
                room *instance;
                list_get_at(rooms, i, (void *) &instance);

                if (hide_full && (instance->room_data->visitors_now >= instance->room_data->visitors_max)) {
                    list_remove(rooms, instance, NULL);
                }
            }

            // Add integer for the amount of public rooms
            om_write_int(navigator, (int) list_size(rooms));  // rooms count
        }

        list_sort_in_place(rooms, room_manager_sort_id);
        list_sort_in_place(rooms, room_manager_sort);

        for (size_t i = 0; i < list_size(rooms); i++) {
            room *instance;
            list_get_at(rooms, i, (void *) &instance);
            room_append_data(instance, navigator, player);
        }

        List *child_categories = category_manager_get_by_parent_id(parent_category->id);

        for (size_t i = 0; i < list_size(child_categories); i++) {
            room_category *category;
            list_get_at(child_categories, i, (void*)&category);

            int current_visitors = category_manager_get_current_vistors(category->id);
            int max_visitors = category_manager_get_max_vistors(category->id);

            if (category_has_access(category, player->details->rank)) {
                om_write_int(navigator, category->id);
                om_write_int(navigator, 0);
                om_write_str(navigator, category->name);
                om_write_int(navigator, current_visitors);
                om_write_int(navigator, max_visitors);
                om_write_int(navigator, parent_category->id); 
            }
        }

        if (recent_rooms != NULL) {
            for (size_t i = 0; i < list_size(recent_rooms); i++) {
                room *instance;
                list_get_at(recent_rooms, i, (void *) &instance);

                if (room_manager_get_by_id(instance->room_id) == NULL) {
                    room_dispose(instance, false);
                }
            }

            list_destroy(recent_rooms);
        }


        list_destroy(rooms);
        list_destroy(child_categories);
    }

    player_send(player, navigator);
    om_cleanup(navigator);
}
