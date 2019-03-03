#include <stdlib.h>
#include <stdio.h>

#include "log.h"

#include "game/items/item_manager.h"
#include "catalogue_package.h"

catalogue_package *catalogue_package_create(char *salecode, int definition_id, int special_sprite_id, int amount) {
    catalogue_package *package = malloc(sizeof(catalogue_package));
    package->sale_code = salecode;
    package->definition_id = definition_id;
    package->special_sprite_id = special_sprite_id;
    package->amount =  amount;
    package->definition = item_manager_get_definition_by_id(definition_id);

    if (package->definition == NULL) {
        log_warn("Warning! The package %s has no valid definition", package->sale_code);
    }

    return package;
}

/**
 * Dispose catalogue package.
 *
 * @param page the catalogue package to dispose
 */
void catalogue_package_dispose(catalogue_package *package) {
    free(package->sale_code);
    free(package);
}