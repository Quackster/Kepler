-- migrate:up
ALTER TABLE `catalogue_items` ADD `is_hidden` TINYINT(1) NOT NULL DEFAULT '0' AFTER `order_id`;

ALTER TABLE `users` CHANGE `figure` `figure` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1000118001270012900121001';

DELETE FROM `rooms_categories` WHERE `rooms_categories`.`id` = 20;
DELETE FROM `rooms_categories` WHERE `rooms_categories`.`id` = 21;

UPDATE `rooms` SET `name` = 'Peaceful Park' WHERE `rooms`.`id` = 32;
UPDATE `rooms` SET `name` = 'Peaceful Park B' WHERE `rooms`.`id` = 33;

UPDATE `rooms` SET `category` = '9' WHERE `rooms`.`id` = 32;
UPDATE `rooms` SET `category` = '9' WHERE `rooms`.`id` = 33;

UPDATE `rooms` SET `category` = '9' WHERE `rooms`.`id` = 34;
UPDATE `rooms` SET `category` = '9' WHERE `rooms`.`id` = 35;

ALTER TABLE `items` ADD `order_id` INT(11) NOT NULL DEFAULT '0' AFTER `id`;

-- Hide Pacha TV jukebox
UPDATE `catalogue_items` SET `is_hidden` = '1' WHERE `catalogue_items`.`id` = 455;

UPDATE `catalogue_pages` SET `body` = 'Bring sound to your room! Purchase a sound machine plus some sample packs and create your own songs to play in your flat!' WHERE `catalogue_pages`.`id` = 11;

-- migrate:down

