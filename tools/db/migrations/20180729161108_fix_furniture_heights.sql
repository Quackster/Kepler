-- migrate:up
UPDATE `items_definitions` SET `top_height` = '0.9' WHERE `items_definitions`.`id` = 202;
UPDATE `items_definitions` SET `top_height` = '1.5' WHERE `items_definitions`.`id` = 197;

UPDATE `items_definitions` SET `top_height` = '1.7' WHERE `items_definitions`.`id` = 19;
UPDATE `items_definitions` SET `top_height` = '1.7' WHERE `items_definitions`.`id` = 20;

UPDATE `items_definitions` SET `top_height` = '1.6' WHERE `items_definitions`.`id` = 66;
UPDATE `items_definitions` SET `top_height` = '1.6' WHERE `items_definitions`.`id` = 65;

UPDATE `items_definitions` SET `top_height` = '1.7' WHERE `items_definitions`.`id` = 30;
UPDATE `items_definitions` SET `top_height` = '1.7' WHERE `items_definitions`.`id` = 22;

UPDATE `items_definitions` SET `top_height` = '1.6' WHERE `items_definitions`.`id` = 68;
UPDATE `items_definitions` SET `top_height` = '1.6' WHERE `items_definitions`.`id` = 69;

UPDATE `items_definitions` SET `top_height` = '1.2' WHERE `items_definitions`.`id` = 230;
UPDATE `items_definitions` SET `top_height` = '1.2' WHERE `items_definitions`.`id` = 231;
UPDATE `items_definitions` SET `top_height` = '1.2' WHERE `items_definitions`.`id` = 229;

UPDATE `items_definitions` SET `top_height` = '0.2' WHERE `items_definitions`.`sprite` LIKE 'sound_set_%';
UPDATE `items_definitions` SET `top_height` = '0.2' WHERE `items_definitions`.`sprite` LIKE 'doormat_plain%';
UPDATE `items_definitions` SET `top_height` = '0.2' WHERE `items_definitions`.`id` = 355;


-- migrate:down

