CREATE TABLE `command_queue` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`executed` TINYINT(1) NOT NULL DEFAULT 0,
	`command` VARCHAR(255) NOT NULL,
	`arguments` VARCHAR(1024) NOT NULL,
	`time` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `users_bans`;
CREATE TABLE `users_bans` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ban_type` enum('MACHINE_ID','IP_ADDRESS','USER_ID') NOT NULL,
  `message` text NOT NULL,
  `banned_until` bigint(11) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `ip` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


ALTER TABLE `users`
ADD `status` varchar(50) NOT NULL DEFAULT 'offline' AFTER `snowstorm_points`,
ADD  `group_id` int(11) NOT NULL DEFAULT 0  AFTER `status`;


CREATE TABLE `tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `group_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `credit_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `type` varchar(255) NOT NULL DEFAULT 'stuff_store',
  `credits` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

COMMIT;
