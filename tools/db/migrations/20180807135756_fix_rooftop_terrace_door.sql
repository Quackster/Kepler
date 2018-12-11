-- migrate:up
UPDATE `rooms_models` SET `door_y` = '11' WHERE `rooms_models`.`model_id` = 'rooftop_2';

-- migrate:down

