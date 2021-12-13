ALTER TABLE `items`
	ADD COLUMN `database_id` VARCHAR(50) NOT NULL DEFAULT LOWER(CONCAT(
    LPAD(HEX(ROUND(rand()*POW(2,32))), 8, '0'), '-',
    LPAD(HEX(ROUND(rand()*POW(2,16))), 4, '0'), '-',
    LPAD(HEX(ROUND(rand()*POW(2,16))), 4, '0'), '-',
    LPAD(HEX(ROUND(rand()*POW(2,16))), 4, '0'), '-',
    LPAD(HEX(ROUND(rand()*POW(2,48))), 12, '0')
)) AFTER `id`;
	
-- Start merge teleporter table
ALTER TABLE `items_teleporter_links`
	ADD COLUMN `database_item_id` VARCHAR(36) NOT NULL AFTER `linked_id`,
	ADD COLUMN `database_linked_id` VARCHAR(36) NOT NULL AFTER `databse_item_id`;
	
UPDATE items_teleporter_links 
	INNER JOIN items
	ON items.id = items_teleporter_links.item_id
SET items_teleporter_links.database_item_id = items.database_id;

UPDATE items_teleporter_links 
	INNER JOIN items
	ON items.id = items_teleporter_links.linked_id
SET items_teleporter_links.database_linked_id = items.database_id;

ALTER TABLE `items_teleporter_links`
	CHANGE COLUMN `database_item_id` `item_id` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_general_ci' FIRST,
	CHANGE COLUMN `database_linked_id` `linked_id` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_general_ci' AFTER `item_id`,
	DROP COLUMN `item_id`,
	DROP COLUMN `linked_id`;
	
-- End merge teleporter link table

-- Start merge moodlight presets table
ALTER TABLE `items_moodlight_presets`
	ADD COLUMN `database_item_id` VARCHAR(36) NOT NULL AFTER `linked_id`;
	
UPDATE items_moodlight_presets 
	INNER JOIN items
	ON items.id = items_moodlight_presets.item_id
SET items_moodlight_presets.database_item_id = items.database_id;

ALTER TABLE `items_moodlight_presets`
	CHANGE COLUMN `database_item_id` `item_id` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_general_ci' FIRST, 
	DROP COLUMN `item_id`;
	
-- End merge moodlight presets table

ALTER TABLE `items`
	CHANGE COLUMN `database_id` `id` VARCHAR(50) NOT NULL DEFAULT lcase(concat(lpad(hex(round(rand() * pow(2,32),0)),8,'0'),'-',lpad(hex(round(rand() * pow(2,16),0)),4,'0'),'-',lpad(hex(round(rand() * pow(2,16),0)),4,'0'),'-',lpad(hex(round(rand() * pow(2,16),0)),4,'0'),'-',lpad(hex(round(rand() * pow(2,48),0)),12,'0'))) COLLATE 'utf8mb4_general_ci' FIRST,
	DROP COLUMN `id`,
	DROP PRIMARY KEY,
	DROP INDEX `id`,
	ADD PRIMARY KEY (`id`),
	ADD UNIQUE INDEX `id` (`id`);