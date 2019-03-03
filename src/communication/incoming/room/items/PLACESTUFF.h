#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "game/inventory/inventory.h"
#include "database/queries/items/item_query.h"

void PLACESTUFF(entity *player, incoming_message *message) {
    if (player->room_user->room == NULL) {
        return;
    }

    if (!room_has_rights(player->room_user->room, player->details->id)) {
        return;
    }

    char *content = im_get_content(message);

    if (content == NULL) {
        return;
    }

    inventory *inv = (inventory *) player->inventory;

    char *str_id = get_argument(content, " ", 0);

    char *str_x = NULL;
    char *str_y = NULL;

    if (str_id == NULL) {
        goto cleanup;
    }

    item *place_item = inventory_get_item(inv, (int)strtol(str_id, NULL, 10));

    if (place_item == NULL) {
        goto cleanup;
    }

    if (place_item->definition->behaviour->is_wall_item) {
        char id_as_string[10];
        sprintf(id_as_string, "%i", place_item->id);

        char *wall_position = strdup(content + strlen(id_as_string) + 1);

        if (place_item->definition->behaviour->is_post_it) {
            // Create postit in database
            char *default_colour = strdup("FFFF33");
            int item_id = item_query_create(player->details->id, 0, place_item->definition->id, 0, 0, 0, 0, default_colour);

            // Create postit instance using the id retrived from the database
            item *sticky = item_create(item_id, 0, player->details->id, place_item->definition->id, 0, 0, 0, wall_position, 0, default_colour);
            room_map_add_item(player->room_user->room, sticky);

            // Update postit amount for the sticky pad pile in hand
            if (is_numeric(place_item->custom_data)) {
                int total_stickies = ((int) strtol(place_item->custom_data, NULL, 10)) - 1;

                // No more postits left, delete.
                if (total_stickies <= 0) {
                    list_remove(inv->items, place_item, NULL);
                    item_query_delete(place_item->id);
                    item_dispose(place_item);
                } else {
                    // Subtract postit count
                    char custom_data[10];
                    sprintf(custom_data, "%i", total_stickies);

                    item_set_custom_data(place_item, strdup(custom_data));
                    item_query_save(place_item);
                }
            }

            goto cleanup;
        }

        place_item->wall_position = wall_position;

    } else {
        str_x = get_argument(content, " ", 1);
        str_y = get_argument(content, " ", 2);

        if (str_x == NULL || str_y == NULL) {
            goto cleanup;
        }

        place_item->position->x = (int) strtol(str_x, NULL, 10);
        place_item->position->y = (int) strtol(str_y, NULL, 10);
        place_item->position->rotation = 0;
    }

    room_map_add_item(player->room_user->room, place_item);
    list_remove(inv->items, place_item, NULL);

    cleanup:
    free(content);
    free(str_id);
    free(str_x);
    free(str_y);
}
