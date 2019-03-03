#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "shared.h"
#include "list.h"
#include "log.h"

#include "communication/messages/outgoing_message.h"

#include "game/player/player.h"
#include "game/items/item_manager.h"
#include "game/catalogue/catalogue_item.h"

#include "util/stringbuilder.h"

#include "catalogue_package.h"

void load_catalogue_packges(catalogue_item *item);

catalogue_item *catalogue_item_create(char *sale_code, int page_id, int order_id, int price, int definition_id, int item_specialspriteid, char *package_name, char *package_description, bool is_package) {
    catalogue_item *item = malloc(sizeof(catalogue_item));
    item->sale_code = strdup(sale_code);
    item->page_id = page_id;
    item->order_id = order_id;
    item->price = price;
    item->definition_id = definition_id;
    item->item_specialspriteid = item_specialspriteid;
    item->is_package = is_package;
    item->definition = item_manager_get_definition_by_id(definition_id);

    list_new(&item->packages);

    if (item->is_package) {
        item->package_name = strdup(package_name);
        item->package_description = strdup(package_description);
        load_catalogue_packges(item);
    } else {
        item->package_name = NULL;
        item->package_description = NULL;
    }

    if (item->definition == NULL) {
        log_warn("WARNING! The catalogue item with sale code '%s' has no definition!", sale_code);
    }
    return item;
}

/**
 * Load catalogue packages through a list.
 *
 * @param item the catalogue items to load the packages for
 */
void load_catalogue_packges(catalogue_item *item) {
    for (size_t i = 0; i < list_size(global.catalogue_manager.packages); i++) {
        catalogue_package *package = NULL;
        list_get_at(global.catalogue_manager.packages, i, (void *) &package);

        if (strcmp(item->sale_code, package->sale_code) == 0) {
            list_add(item->packages, package);
        }
    }

}

char *catalogue_item_get_name(catalogue_item *item) {
    if (item->is_package) {
        return strdup(item->package_name);
    } else {
        return item_definition_get_name(item->definition, item->item_specialspriteid);
    }
}

char *catalogue_item_get_description(catalogue_item *item) {
    if (item->is_package) {
        return strdup(item->package_description);
    } else {
        return item_definition_get_desc(item->definition, item->item_specialspriteid);
    }
}

char *catalogue_item_get_type(catalogue_item *item) {
    if (item->is_package) {
        return strdup("d");
    } else {
        if (item->definition->behaviour->is_wall_item) {
            return strdup("i");
        } else {
            return strdup("s");
        }
    }
}

char *catalogue_item_get_icon(catalogue_item *item) {
    if (item->is_package) {
        return strdup("");
    } else {
        return item_definition_get_icon(item->definition, item->item_specialspriteid);
    }
}

char *catalogue_item_get_size(catalogue_item *item) {
    if (item->is_package || item->definition->behaviour->is_wall_item) {
        return strdup("");
    } else {
        return strdup("0");
    }
}

char *catalogue_item_get_dimensions(catalogue_item *item) {
    if (item->is_package || item->definition->behaviour->is_wall_item) {
        return strdup("");
    } else {
        stringbuilder *sb = sb_create();
        sb_add_int(sb, item->definition->length);
        sb_add_string(sb, ",");
        sb_add_int(sb, item->definition->width);

        char *dimensions = strdup(sb->data);
        sb_cleanup(sb);
        return dimensions;
    }
}

void catalogue_item_dispose(catalogue_item *item) {
    free(item->sale_code);

    if (item->is_package) {
        free(item->package_name);
        free(item->package_description);
    }

    list_destroy(item->packages);
    free(item);

}