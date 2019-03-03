#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "item_behaviour.h"
#include "item_definition.h"

item_behaviour *item_behaviour_create() {
    item_behaviour *behaviour = malloc(sizeof(item_behaviour));
    behaviour->is_wall_item = false;
    behaviour->is_solid = false;
    behaviour->can_sit_on_top = false;
    behaviour->can_lay_on_top = false;
    behaviour->can_stand_on_top = false;
    behaviour->can_stack_on_top = false;
    behaviour->is_roller = false;
    behaviour->is_public_space_object = false;
    behaviour->isInvisible = false;
    behaviour->requires_rights_for_interaction = false;
    behaviour->requiresTouchingForInteraction = false;
    behaviour->custom_data_true_false = false;
    behaviour->custom_data_on_off = false;
    behaviour->custom_data_numeric_on_off = false;
    behaviour->custom_data_numeric_state = false;
    behaviour->is_decoration = false;
    behaviour->is_post_it = false;
    behaviour->is_door = false;
    behaviour->isTeleporter = false;
    behaviour->isDice = false;
    behaviour->is_prize_trophy = false;
    behaviour->is_redeemable = false;
    behaviour->isSoundMachine = false;
    behaviour->isSoundMachineSampleSet = false;
    behaviour->has_extra_parameter = false;
    return behaviour;
}

item_behaviour *item_behaviour_parse(item_definition *def) {
    item_behaviour *behaviour = item_behaviour_create();

    if (def->behaviour_data == NULL) {
        return behaviour;
    }

    for (int i = 0; i < strlen(def->behaviour_data); i++) {
        char c = def->behaviour_data[i];

        if (c == 'W') {
            behaviour->is_wall_item = true;
        }

        if (c == 'S') {
            behaviour->is_solid = true;
        }

        if (c == 'C') {
            behaviour->can_sit_on_top = true;
        }

        if (c == 'B') {
            behaviour->can_lay_on_top = true;
        }

        if (c == 'K') {
            behaviour->can_stand_on_top = true;
        }

        if (c == 'R') {
            behaviour->is_roller = true;
        }

        if (c == 'P') {
            behaviour->is_public_space_object = true;
        }

        if (c == 'I') {
            behaviour->isInvisible = true;
        }

        if (c == 'G') {
            behaviour->requires_rights_for_interaction = true;
        }

        if (c == 'T') {
            behaviour->requiresTouchingForInteraction = true;
        }

        if (c == 'U') {
            behaviour->custom_data_true_false = true;
        }

        if (c == 'O') {
            behaviour->custom_data_on_off = true;
        }

        if (c == 'M') {
            behaviour->custom_data_numeric_on_off = true;
        }

        if (c == 'Z') {
            behaviour->custom_data_numeric_state = true;
        }

        if (c == 'H') {
            behaviour->can_stack_on_top = true;
        }

        if (c == 'V') {
            behaviour->is_decoration = true;
        }

        if (c == 'J') {
            behaviour->is_post_it = true;
        }

        if (c == 'D') {
            behaviour->is_door = true;
        }

        if (c == 'X') {
            behaviour->isTeleporter = true;
        }

        if (c == 'F') {
            behaviour->isDice = true;
        }

        if (c == 'Y') {
            behaviour->is_prize_trophy = true;
        }

        if (c == 'Q') {
            behaviour->is_redeemable = true;
        }

        if (c == 'A') {
            behaviour->isSoundMachine = true;
        }

        if (c == 'N') {
            behaviour->isSoundMachineSampleSet = true;
        }
    }

    return behaviour;
}

