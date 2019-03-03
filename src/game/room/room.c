#include "shared.h"
#include "log.h"
#include "list.h"

#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "room_user.h"

#include "game/pathfinder/coord.h"
#include "game/room/mapping/room_model.h"
#include "game/room/mapping/room_map.h"

#include "game/room/manager/room_item_manager.h"
#include "game/room/manager/room_entity_manager.h"
#include "game/room/room_task.h"

#include "game/player/player.h"
#include "game/items/item.h"

#include "database/queries/player_query.h"
#include "database/queries/rooms/room_rights_query.h"

#include "util/stringbuilder.h"

/**
 * Create a room instance.
 *
 * @param room_id the room id
 * @return the room instance
 */
room *room_create(int room_id) {
    room *instance = malloc(sizeof(room));
    instance->room_id = room_id;
    instance->connected_room = NULL;
    instance->connected_room_hide = false;
    instance->room_data = NULL;
    instance->room_map = NULL;
    instance->walk_process_timer = NULL;
    instance->status_process_timer = NULL;
    instance->roller_process_timer = NULL;
    list_new(&instance->users);
    list_new(&instance->items);
    instance->rights = room_query_rights(room_id);
    instance->tick = 0;
    return instance;
}

/**
 * Create a room data instance.
 *
 * @param id
 * @param owner_id
 * @param category
 * @param name
 * @param description
 * @param model
 * @param ccts
 * @param wallpaper
 * @param floor
 * @param showname
 * @param superusers
 * @param accesstype
 * @param password
 * @param visitors_now
 * @param visitors_max
 * @return
 */
room_data *room_create_data(room *room, int id, int owner_id, int category, char *name, char *description, char *model, char *ccts, int wallpaper, int floor, int showname, bool superusers, int accesstype, char *password, int visitors_now, int visitors_max) {
    room_data *data = malloc(sizeof(room_data));
    data->id = id;
    data->owner_id = owner_id;
    data->owner_name = player_query_username(owner_id);
    data->category = category;
    data->name = strdup(name);
    data->description = strdup(description);
    data->model_data = model_manager_get(model);
    data->model = data->model_data->model_name;

    if (ccts == NULL) {
        data->ccts = strdup("");
    } else {
        data->ccts = strdup(ccts);
    }

    data->wallpaper = wallpaper;
    data->floor = floor;
    data->show_name = showname;
    data->superusers = superusers;
    data->accesstype = accesstype;
    data->password = strdup(password);
    data->visitors_now = visitors_now;
    data->visitors_max = visitors_max;

    List *public_items = data->model_data->public_items;

    if (list_size(public_items) > 0) {
        ListIter iter;
        list_iter_init(&iter, public_items);

        item *room_item;

        while (list_iter_next(&iter, (void*)&room_item) != CC_ITER_END) {
            room_item->room_id = id;
            list_add(room->items, room_item);
        }
    }

    return data;
}

/**
 * Create a room rights entry that can be added into the rights list
 * of a room instance.
 *
 * @param user_id the user id who has a right
 * @return the rights entry struct
 */
rights_entry *rights_entry_create(int user_id) {
    rights_entry *entry = malloc(sizeof(rights_entry));
    entry->user_id = user_id;
    return entry;
}

/**
 * Append room information data, useful for various navigator packets.
 *
 * @param instance the instance to append
 * @param navigator the navigator packet instance
 * @param player_id the player who requests the room
 */
void room_append_data(room *instance, outgoing_message *navigator, entity *player) {
    if (instance->connected_room_hide) {
        return;
    }

    if (instance->room_data->owner_id == 0) {
        om_write_int(navigator, instance->room_data->id); // rooms id
        om_write_int(navigator, 1);
        om_write_str(navigator, instance->room_data->name);

        int visitors_now = instance->room_data->visitors_now;
        int visitors_max = instance->room_data->visitors_max;

        if (instance->connected_room != NULL) {
            visitors_now += instance->connected_room->room_data->visitors_now;
            visitors_max += instance->connected_room->room_data->visitors_max;
        }

        om_write_int(navigator, visitors_now); // current visitors
        om_write_int(navigator, visitors_max); // max vistors
        om_write_int(navigator, instance->room_data->category); // category id
        om_write_str(navigator, instance->room_data->description); // description
        om_write_int(navigator, instance->room_data->id); // rooms id
        om_write_int(navigator, 0);
        om_write_str(navigator, instance->room_data->ccts);
        om_write_int(navigator, 0);
        om_write_int(navigator, 1);
    } else {
        om_write_int(navigator, instance->room_data->id); // rooms id
        om_write_str(navigator, instance->room_data->name);

        if (player->details->id == instance->room_data->owner_id || instance->room_data->show_name == true || player_has_fuse(player, "fuse_see_all_roomowners")) {
            om_write_str(navigator, instance->room_data->owner_name); // rooms owner
        } else {
            om_write_str(navigator, "-"); // rooms owner
        }

        if (instance->room_data->accesstype == 2) {
            om_write_str(navigator, "password");
        }

        if (instance->room_data->accesstype == 1) {
            om_write_str(navigator, "closed");
        }

        if (instance->room_data->accesstype == 0) {
            om_write_str(navigator, "open");
        }

        om_write_int(navigator, instance->room_data->visitors_now); // current visitors
        om_write_int(navigator, instance->room_data->visitors_max); // max vistors
        om_write_str(navigator, instance->room_data->description); // description
    }
}

/**
 * Used to load data if they're the first to enter the room.
 *
 * @param room the room to load the data for
 */
void room_load_data(room *room) {
    room_item_manager_load(room);
    room_map_init(room);

    log_info("Room %i loaded.", room->room_id);
}

/**
 * Kick all users from the current room
 * @param room
 */
void room_kickall(room *room) {
    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *user;
        list_get_at(room->users, i, (void *) &user);
        room_leave(room, user, true);
    }
}

/**
 * Get if the user id is owner of the room.
 *
 * @param room the room to check owner for
 * @param user_id the id to check against
 * @return true, if successful
 */
bool room_is_owner(room *room, int user_id) {
    return room->room_data->owner_id == user_id;
}

/**
 * Find a room rights entry by given room and user id.
 *
 * @param room the room to find the rights entry for
 * @param user_id the user id to search for
 * @return the room rights entry
 */
rights_entry *rights_entry_find(room *room, int user_id) {
    for (size_t i = 0; i < list_size(room->rights); i++) {
        rights_entry *rights_entry;
        list_get_at(room->rights, i, (void *) &rights_entry);

        if (rights_entry->user_id == user_id) {
            return rights_entry;
        }
    }

    return NULL;
}

/**
 * Get if the player has rights.
 *
 * @param room the room to check rights for
 * @param user_id the user id to check if they have rights
 * @return true, if successful
 */
bool room_has_rights(room *room, int user_id) {
    if (room->room_data->owner_id == user_id) { // Of course rooms owners have rights, duh!
        return true;
    }

    if (room->room_data->superusers) {
        return true;
    }

    rights_entry *entry = rights_entry_find(room, user_id);

    if (entry != NULL) {
        return true;
    }

    return false;
}

/**
 * Refresh the room rights for the user.
 *
 * @param room the room to refresh inside for
 * @param player the player to refresh the rights for
 */
void room_refresh_rights(room *room, entity *player) {
    if (player == NULL) {
        return;
    }

    char rights_value[15];
    strcpy(rights_value, "");

    outgoing_message *om;

    if (room_has_rights(room, player->details->id)) {
        om = om_create(42); // "@j"
        player_send(player, om);
        om_cleanup(om);
    }

    if (room_is_owner(room, player->details->id)) {
        om = om_create(47); // "@o"
        player_send(player, om);
        om_cleanup(om);

        strcpy(rights_value, " useradmin");
    }

    room_user *room_entity = (room_user*) player->room_user;
    room_user_remove_status(room_entity, "flatctrl");

    if (room_has_rights(room, player->details->id) || room_is_owner(room, player->details->id)) {
        room_user_add_status(room_entity, "flatctrl", rights_value, -1, "", -1, -1);
        room_entity->needs_update = true;
    }
}

/**
 * Send an outgoing message to all the room users.
 *
 * @param room the room
 * @param message the outgoing message to send
 */
void room_send(room *room, outgoing_message *message) {
    om_finalise(message);

    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *player;
        list_get_at(room->users, i, (void*)&player);

        if (player->entity_type != PLAYER_TYPE) {
            continue;
        }

        player_send(player, message);
    }
}

/**
 * Send an outgoing message to room users with rights.
 *
 * @param room the room
 * @param message the outgoing message to send
 */
bool room_send_with_rights(room *room, outgoing_message *message) {
    bool sent_message_to_users = false;

    om_finalise(message);

    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *player;
        list_get_at(room->users, i, (void*)&player);

        if (player->entity_type != PLAYER_TYPE) {
            continue;
        }

        if (room_has_rights(room, player->details->id)) {
            player_send(player, message);
            sent_message_to_users = true;
        }
    }

    return sent_message_to_users;
}

/**
 * Find nearby players with a given room, position and distance, will not be inclusive of the
 * room user in the list if the room_user parameter is not NULL.
 *
 * @param room the room to locate the users inside
 * @param room_user the given room user to exclude (if not NULL)
 * @param position the position to search within
 * @param distance the distance from the position to search inside
 * @return the list of nearby users
 */
List *room_nearby_players(room *room, room_user *room_user, coord *position, int distance) {
    List *players;
    list_new(&players);

    for (size_t i = 0; i < list_size(room->users); i++) {
        entity *player;
        list_get_at(room->users, i, (void *) &player);

        if (room_user != NULL && player->room_user->instance_id == room_user->instance_id) {
            continue;
        }

        coord *current_point = position;
        coord *player_point = player->room_user->position;

        if (coord_distance_squared(current_point, player_point) <= distance) {
            list_add(players, player);
        }
    }

    return players;
}

/**
 * Cleanup a room instance.
 *
 * @param room the room instance.
 */
void room_dispose(room *room, bool force_dispose) {
    if (list_size(room->users) > 0) {
        return;
    }

    room_stop_tasks(room);
    room->tick = 0;

    room_map_destroy(room);

    if (room->room_data->owner_id == 0 && !force_dispose) { // model is a public rooms model
        return; // Prevent public rooms
    }

    room_item_manager_dispose(room);

    if (!force_dispose && player_manager_find_by_id(room->room_data->owner_id) != NULL) {
        return;
    }

    room_manager_remove(room->room_id);

    for (size_t i = 0; i < list_size(room->rights); i++) {
        rights_entry *rights_entry;
        list_get_at(room->rights, i, (void *) &rights_entry);
        free(rights_entry);
    }

    list_destroy(room->rights);
    list_destroy(room->users);
    list_destroy(room->items);

    room->users = NULL;
    room->rights = NULL;
    room->users = NULL;

    if (room->room_data != NULL) {
        free(room->room_data->name);
        free(room->room_data->owner_name);
        free(room->room_data->description);
        free(room->room_data->ccts);
        free(room->room_data->password);
        free(room->room_data);
        room->room_data = NULL;
    }

    free(room);
}

outgoing_message *room_lingo_command(char *command) {
    // A]2147483647{2}spotlight{2}SSIIH-1.00{2}{2}{2}HvoiceSpeak("test"){2}{1}
    outgoing_message *om = om_create(93); // "Bf"
    om_write_str(om, "2147483647");
    om_write_str(om, "spotlight");
    om_write_str(om, "SSIIH-1.00");
    om_write_str(om, "");
    om_write_str(om, "");
    sb_add_string(om->sb, "H");
    om_write_str(om, command);
    om_finalise(om);

    return om;
}