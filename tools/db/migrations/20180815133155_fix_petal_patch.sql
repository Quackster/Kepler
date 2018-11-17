-- migrate:up
UPDATE `items_definitions` SET `behaviour` = 'can_stand_on_top,can_stack_on_top,place_roller_on_top' WHERE `items_definitions`.`id` = 717;
UPDATE `items_definitions` SET `top_height` = '0.4' WHERE `items_definitions`.`id` = 717;

-- migrate:down

