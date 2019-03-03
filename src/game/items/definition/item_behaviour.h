#ifndef ITEM_BEHAVIOUR_H
#define ITEM_BEHAVIOUR_H

#include <stdbool.h>

typedef struct item_definition_s item_definition;

typedef struct item_behaviour_s {
    bool is_wall_item;
    bool is_solid;
    bool can_sit_on_top;
    bool can_lay_on_top;
    bool can_stand_on_top;
    bool can_stack_on_top;
    bool is_roller;
    bool is_public_space_object;
    bool isInvisible;
    bool requires_rights_for_interaction;
    bool requiresTouchingForInteraction;
    bool custom_data_true_false;
    bool custom_data_on_off;
    bool custom_data_numeric_on_off;
    bool custom_data_numeric_state;
    bool is_decoration;
    bool is_post_it;
    bool is_door;
    bool isTeleporter;
    bool isDice;
    bool is_prize_trophy;
    bool is_redeemable;
    bool isSoundMachine;
    bool isSoundMachineSampleSet;
    bool has_extra_parameter;
} item_behaviour;

item_behaviour *item_behaviour_create();
item_behaviour *item_behaviour_parse(item_definition *def);

#endif