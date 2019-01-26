INSERT INTO `items_definitions` (`id`, `sprite`, `sprite_id`, `name`, `description`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES
(422, 'photo', 11221, 'Photo','Photo from Habbo', '', 0, 0, 0, 'photo,wall_item');

INSERT INTO `items_definitions` (`id`, `sprite`, `sprite_id`, `name`, `description`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES
(415, 'present_gen1', 1372, '', 'Gift','What\'s inside?', 1, 1, 1, 'solid,present,can_stack_on_top'),
(416, 'present_gen2', 1373, '', 'Gift','What\'s inside?', 1, 1, 1, 'solid,present,can_stack_on_top'),
(417, 'present_gen3', 1374, '', 'Gift','What\'s inside?', 1, 1, 1, 'solid,present,can_stack_on_top'),
(418, 'present_gen4', 1375, '', 'Gift','What\'s inside?', 1, 1, 1, 'solid,present,can_stack_on_top'),
(419, 'present_gen5', 1376, '', 'Gift','What\'s inside?', 1, 1, 1, 'solid,present,can_stack_on_top'),
(420, 'present_gen6', 1377, '', 'Gift','What\'s inside?', 1, 1, 1, 'solid,present,can_stack_on_top');

ALTER TABLE `items` ADD `is_hidden` TINYINT(1) NOT NULL DEFAULT '0' AFTER `custom_data`;

UPDATE `items_definitions` SET `colour` = '0,0,0', `name` = 'Gift', `description` = 'What\'s inside?' WHERE `items_definitions`.`id` = 415;
UPDATE `items_definitions` SET `colour` = '0,0,0', `name` = 'Gift', `description` = 'What\'s inside?' WHERE `items_definitions`.`id` = 416;
UPDATE `items_definitions` SET `colour` = '0,0,0', `name` = 'Gift', `description` = 'What\'s inside?' WHERE `items_definitions`.`id` = 417;
UPDATE `items_definitions` SET `colour` = '0,0,0', `name` = 'Gift', `description` = 'What\'s inside?' WHERE `items_definitions`.`id` = 418;
UPDATE `items_definitions` SET `colour` = '0,0,0', `name` = 'Gift', `description` = 'What\'s inside?' WHERE `items_definitions`.`id` = 419;
UPDATE `items_definitions` SET `colour` = '0,0,0', `name` = 'Gift', `description` = 'What\'s inside?' WHERE `items_definitions`.`id` = 420;

INSERT INTO `items_definitions` (`id`, `sprite`, `sprite_id`, `name`, `description`, `colour`, `length`, `width`, `top_height`, `max_status`, `behaviour`, `interactor`, `is_tradable`, `is_recyclable`, `drink_ids`) VALUES (NULL, 'song_disk', '1355', 'Traxdisc', 'Burn, baby burn', '0,0,0', '1', '1', '0.1', '0', 'solid,song_disk', 'default', '1', '1', '');

CREATE TABLE `soundmachine_disks` (
  `item_id` bigint(11) NOT NULL,
  `soundmachine_id` int(11) NOT NULL DEFAULT 0,
  `slot_id` int(11) NOT NULL,
  `song_id` int(11) NOT NULL,
  `burned_at` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

UPDATE `items_definitions` SET `behaviour` = 'solid,custom_data_true_false' WHERE `items_definitions`.`id` = 718;
UPDATE `items_definitions` SET `drink_ids` = '1' WHERE `items_definitions`.`id` = 752;
UPDATE `items_definitions` SET `interactor` = 'vending_machine' WHERE `items_definitions`.`id` = 220;
UPDATE `items_definitions` SET `drink_ids` = '24' WHERE `items_definitions`.`id` = 220;
UPDATE `items_definitions` SET `length` = '3', `width` = '1' WHERE `items_definitions`.`id` = 1369;
UPDATE `items_definitions` SET `length` = '3', `width` = '1' WHERE `items_definitions`.`id` = 1364;

-- Set these as hidden as they do not work for v14
UPDATE `catalogue_items` SET `is_hidden` = '1' WHERE `catalogue_items`.`id` = 623;
UPDATE `catalogue_items` SET `is_hidden` = '1' WHERE `catalogue_items`.`id` = 624;

-- Fix description/name in catalogue
UPDATE `catalogue_items` SET `name` = 'Siva Poster', `description` = 'The Auspicious One' WHERE `catalogue_items`.`id` = 296;