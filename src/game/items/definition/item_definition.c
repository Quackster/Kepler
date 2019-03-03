#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <game/texts/external_texts_manager.h>

#include "item_definition.h"
#include "item_behaviour.h"
#include "util/stringbuilder.h"

item_definition *item_definition_create(int id, int cast_directory, char *sprite, char *colour, int length, int width, double top_height, char *behaviour) {
    item_definition *def = malloc(sizeof(item_definition));
    def->id = id;
    def->cast_directory = cast_directory;
    def->sprite = strdup(sprite);
    def->colour = strdup(colour);
    def->length = length;
    def->width = width;
    def->stack_height = top_height;
    def->top_height = top_height;
    def->behaviour_data = strdup(behaviour);
    def->behaviour = item_behaviour_parse(def);

    if (!def->behaviour->can_stack_on_top) {
        def->stack_height = 0;
    }

    if (def->stack_height == 0) {
        def->stack_height = 0.001;
    }

    return def;
}

item_definition *item_definition_create_blank() {
    item_definition *def = malloc(sizeof(item_definition));
    def->id = -1;
    def->cast_directory = -1;
    def->sprite = NULL;
    def->colour = NULL;
    def->length = 1;
    def->width = 1;
    def->stack_height = 0;
    def->top_height = 1;
    def->behaviour_data = NULL;
    def->behaviour = item_behaviour_parse(def);

    if (def->stack_height == 0) {
        def->stack_height = 0.001;
    }

    return def;
}

bool item_contains_custom_data(item_definition *definition) {
    return definition->behaviour->custom_data_numeric_on_off
            || definition->behaviour->custom_data_true_false
            || definition->behaviour->custom_data_numeric_state
            || definition->behaviour->custom_data_on_off
            || definition->behaviour->is_door;
}

/**
 * Get the item name by creating an external text key and reading external text entries.
 *
 * @param definition the item definition
 * @param special_sprite_id the special sprite id
 * @return the name
 */
char *item_definition_get_name(item_definition *definition, int special_sprite_id) {
    if (definition->behaviour->is_decoration) {
        return strdup(definition->sprite);
    } else {
        char *external_text_key = item_definition_get_text_key(definition, special_sprite_id);

        stringbuilder *sb = sb_create();
        sb_add_string(sb, external_text_key);
        sb_add_string(sb, "_name");

        char *name = texts_manager_get_value_by_id(sb->data);
        sb_cleanup(sb);

        if (name == NULL) {
            name = strdup("null");
        }

        free(external_text_key);
        return strdup(name);
    }
}

/**
 * Get the item description by creating an external text key and reading external text entries.
 *
 * @param definition the item definition
 * @param special_sprite_id the special sprite id
 * @return the description
 */
char *item_definition_get_desc(item_definition *definition, int special_sprite_id) {
    if (definition->behaviour->is_decoration) {
        return strdup(definition->sprite);
    } else {
        char *external_text_key = item_definition_get_text_key(definition, special_sprite_id);

        stringbuilder *sb = sb_create();
        sb_add_string(sb, external_text_key);
        sb_add_string(sb, "_desc");

        char *desc = texts_manager_get_value_by_id(sb->data);
        sb_cleanup(sb);

        if (desc == NULL) {
            desc = strdup("null");
        }

        free(external_text_key);
        return strdup(desc);
    }
}

/**
 * Create the catalogue icon through using the special sprite id.
 *
 * @param definition the item definition
 * @param special_sprite_id the special sprite id
 * @return the catalogue icon
 */
char *item_definition_get_icon(item_definition *definition, int special_sprite_id) {
    stringbuilder *sb = sb_create();
    sb_add_string(sb, definition->sprite);

    if (special_sprite_id > 0) {
        sb_add_string(sb, " ");
        sb_add_int(sb, special_sprite_id);
    }

    char *name = strdup(sb->data);
    sb_cleanup(sb);

    return name;
}


/**
 * Get external text key by definition.
 *
 * @param definition the item definition
 * @param special_sprite_id the special sprite id
 * @return the external text key
 */
char *item_definition_get_text_key(item_definition *definition, int special_sprite_id) {
    stringbuilder *sb = sb_create();

    if (special_sprite_id == 0) {
        if (definition->behaviour->is_wall_item) {
            sb_add_string(sb, "wallitem");
        } else {
            sb_add_string(sb, "furni");
        }

        sb_add_string(sb, "_");
    }

    sb_add_string(sb, definition->sprite);

    if (special_sprite_id > 0) {
        sb_add_string(sb, "_");
        sb_add_int(sb, special_sprite_id);
    }

    char *text_key = strdup(sb->data);
    sb_cleanup(sb);

    return text_key;
}

void item_definition_dispose(item_definition *def) {
    free(def->sprite);
    free(def->behaviour);
    free(def->behaviour_data);
    free(def->colour);
    free(def);
}