CREATE TABLE `users_club_gifts` (
  `user_id` int(11) NOT NULL,
  `sprite` varchar(50) NOT NULL,
  `date_received` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `users_mutes` (
  `user_id` int(11) NOT NULL,
  `muted_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `users` ADD `club_gift_due` BIGINT(11) NOT NULL DEFAULT '0' AFTER `club_expiration`;

UPDATE catalogue_items SET sale_code = (SELECT sprite FROM items_definitions WHERE items_definitions.id = catalogue_items.definition_id) WHERE page_id = 27 AND is_package = 0

ALTER TABLE `settings` CHANGE `value` `value` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '';