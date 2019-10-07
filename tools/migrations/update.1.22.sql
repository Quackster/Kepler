UPDATE items_definitions SET is_recyclable = 0 WHERE id = 415;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 416;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 417;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 418;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 419;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 420;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 422;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 1412;

CREATE TABLE `recycler_sessions` (
	`user_id` INT NULL,
	`reward_id` INT NULL,
	`session_started` BIGINT NULL DEFAULT NULL
)
COLLATE='utf8mb4_general_ci'
;

ALTER TABLE `recycler_sessions`
	CHANGE COLUMN `session_started` `session_started` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() AFTER `reward_id`;
	
ALTER TABLE `recycler_sessions`
	ALTER `user_id` DROP DEFAULT,
	ALTER `reward_id` DROP DEFAULT;
ALTER TABLE `recycler_sessions`
	CHANGE COLUMN `user_id` `user_id` INT(11) NOT NULL FIRST,
	CHANGE COLUMN `reward_id` `reward_id` INT(11) NOT NULL AFTER `user_id`,
	ADD PRIMARY KEY (`user_id`);