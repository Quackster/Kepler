-- migrate:up
UPDATE `rooms_models` SET `door_x` = '0', `door_y` = '0' WHERE `rooms_models`.`model_id` = 'hallA';
UPDATE `rooms_models` SET `door_x` = '1', `door_y` = '0' WHERE `rooms_models`.`model_id` = 'hallB';
UPDATE `rooms_models` SET `door_x` = '0', `door_y` = '0' WHERE `rooms_models`.`model_id` = 'hallC';
UPDATE `rooms_models` SET `door_x` = '0', `door_y` = '0' WHERE `rooms_models`.`model_id` = 'hallD';

-- migrate:down

