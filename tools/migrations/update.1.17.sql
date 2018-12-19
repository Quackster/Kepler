TRUNCATE `users_room_votes`;

ALTER TABLE `users_room_votes` ADD `expire_time` BIGINT(11) NOT NULL DEFAULT '0' AFTER `vote`;
ALTER TABLE `rooms` ADD `rating` INT(11) NOT NULL DEFAULT '0' AFTER `visitors_max`;

