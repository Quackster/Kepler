ALTER TABLE `users` CHANGE `sso_ticket` `sso_ticket` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL;

DROP TABLE IF EXISTS `games_player_spawns`;
DROP TABLE IF EXISTS `vouchers`;
DROP TABLE IF EXISTS `vouchers_history`;
DROP TABLE IF EXISTS `vouchers_items`;

CREATE TABLE `games_player_spawns` (
  `type` enum('battleball','snowstorm') NOT NULL DEFAULT 'battleball',
  `map_id` int(11) NOT NULL,
  `team_id` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `rotation` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `games_player_spawns` (`type`, `map_id`, `team_id`, `x`, `y`, `rotation`) VALUES
('battleball', 5, 0, 22, 14, 6),
('battleball', 5, 0, 22, 13, 6),
('battleball', 5, 0, 22, 12, 6),
('battleball', 5, 0, 22, 11, 6),
('battleball', 5, 0, 22, 10, 6),
('battleball', 5, 0, 22, 9, 6),
('battleball', 5, 3, 9, 0, 4),
('battleball', 5, 3, 10, 0, 4),
('battleball', 5, 3, 11, 0, 4),
('battleball', 5, 3, 12, 0, 4),
('battleball', 5, 3, 13, 0, 4),
('battleball', 5, 3, 14, 0, 4),
('battleball', 5, 2, 9, 22, 0),
('battleball', 5, 2, 10, 22, 0),
('battleball', 5, 2, 11, 22, 0),
('battleball', 5, 2, 12, 22, 0),
('battleball', 5, 2, 13, 22, 0),
('battleball', 5, 2, 14, 22, 0),
('battleball', 5, 1, 0, 14, 2),
('battleball', 5, 1, 0, 13, 2),
('battleball', 5, 1, 0, 12, 2),
('battleball', 5, 1, 0, 11, 2),
('battleball', 5, 1, 0, 10, 2),
('battleball', 5, 1, 0, 9, 2),
('battleball', 3, 0, 21, 11, 6),
('battleball', 3, 0, 21, 12, 6),
('battleball', 3, 0, 21, 13, 6),
('battleball', 3, 0, 21, 14, 6),
('battleball', 3, 0, 21, 15, 6),
('battleball', 3, 0, 21, 16, 6),
('battleball', 3, 1, 7, 16, 2),
('battleball', 3, 1, 7, 15, 2),
('battleball', 3, 1, 7, 14, 2),
('battleball', 3, 1, 7, 13, 2),
('battleball', 3, 1, 7, 12, 2),
('battleball', 3, 1, 7, 11, 2),
('battleball', 3, 2, 11, 7, 4),
('battleball', 3, 2, 12, 7, 4),
('battleball', 3, 2, 13, 7, 4),
('battleball', 3, 2, 14, 7, 4),
('battleball', 3, 2, 15, 7, 4),
('battleball', 3, 2, 16, 7, 4),
('battleball', 3, 3, 16, 21, 0),
('battleball', 3, 3, 15, 21, 0),
('battleball', 3, 3, 14, 21, 0),
('battleball', 3, 3, 13, 21, 0),
('battleball', 3, 3, 12, 21, 0),
('battleball', 3, 3, 11, 21, 0),
('battleball', 1, 0, 0, 13, 2),
('battleball', 1, 0, 0, 14, 2),
('battleball', 1, 0, 0, 15, 2),
('battleball', 1, 0, 0, 16, 2),
('battleball', 1, 0, 0, 17, 2),
('battleball', 1, 0, 0, 18, 2),
('battleball', 1, 1, 27, 9, 6),
('battleball', 1, 1, 27, 10, 6),
('battleball', 1, 1, 27, 11, 6),
('battleball', 1, 1, 27, 12, 6),
('battleball', 1, 1, 27, 13, 6),
('battleball', 1, 1, 27, 14, 6),
('battleball', 1, 3, 13, 0, 4),
('battleball', 1, 3, 14, 0, 4),
('battleball', 1, 3, 15, 0, 4),
('battleball', 1, 3, 16, 0, 4),
('battleball', 1, 3, 17, 0, 4),
('battleball', 1, 3, 18, 0, 4),
('battleball', 1, 2, 14, 27, 0),
('battleball', 1, 2, 13, 27, 0),
('battleball', 1, 2, 12, 27, 0),
('battleball', 1, 2, 11, 27, 0),
('battleball', 1, 2, 10, 27, 0),
('battleball', 1, 2, 9, 27, 0),
('battleball', 2, 0, 0, 7, 2),
('battleball', 2, 0, 0, 8, 2),
('battleball', 2, 0, 0, 9, 2),
('battleball', 2, 0, 0, 10, 2),
('battleball', 2, 0, 0, 11, 2),
('battleball', 2, 0, 0, 12, 2),
('battleball', 2, 2, 14, 12, 6),
('battleball', 2, 2, 14, 11, 6),
('battleball', 2, 2, 14, 10, 6),
('battleball', 2, 2, 14, 9, 6),
('battleball', 2, 2, 14, 8, 6),
('battleball', 2, 2, 14, 7, 6),
('battleball', 2, 3, 18, 8, 2),
('battleball', 2, 3, 18, 9, 2),
('battleball', 2, 3, 18, 10, 2),
('battleball', 2, 3, 18, 11, 2),
('battleball', 2, 3, 18, 12, 2),
('battleball', 2, 3, 18, 13, 2),
('battleball', 2, 1, 32, 7, 6),
('battleball', 2, 1, 32, 8, 6),
('battleball', 2, 1, 32, 9, 6),
('battleball', 2, 1, 32, 10, 6),
('battleball', 2, 1, 32, 11, 6),
('battleball', 2, 1, 32, 12, 6),
('battleball', 4, 0, 12, 16, 0),
('battleball', 4, 0, 13, 16, 0),
('battleball', 4, 0, 14, 16, 0),
('battleball', 4, 0, 15, 16, 0),
('battleball', 4, 0, 16, 16, 0),
('battleball', 4, 0, 17, 16, 0),
('battleball', 4, 1, 17, 0, 4),
('battleball', 4, 1, 16, 0, 4),
('battleball', 4, 1, 15, 0, 4),
('battleball', 4, 1, 14, 0, 4),
('battleball', 4, 1, 13, 0, 4),
('battleball', 4, 1, 12, 0, 4),
('battleball', 4, 2, 7, 5, 2),
('battleball', 4, 2, 7, 6, 2),
('battleball', 4, 2, 7, 7, 2),
('battleball', 4, 2, 7, 8, 2),
('battleball', 4, 2, 7, 9, 2),
('battleball', 4, 2, 7, 10, 2),
('battleball', 4, 3, 21, 5, 6),
('battleball', 4, 3, 21, 6, 6),
('battleball', 4, 3, 21, 7, 6),
('battleball', 4, 3, 21, 8, 6),
('battleball', 4, 3, 21, 9, 6),
('battleball', 4, 3, 21, 10, 6);

CREATE TABLE `vouchers` (
  `voucher_code` varchar(100) NOT NULL,
  `credits` int(11) NOT NULL DEFAULT 0,
  `expiry_date` datetime DEFAULT NULL,
  `is_single_use` tinyint(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

CREATE TABLE `vouchers_history` (
  `voucher_code` varchar(100) NOT NULL,
  `user_id` int(11) NOT NULL,
  `used_at` datetime NOT NULL DEFAULT current_timestamp(),
  `credits_redeemed` int(11) DEFAULT NULL,
  `items_redeemed` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

CREATE TABLE `vouchers_items` (
  `voucher_code` varchar(100) NOT NULL,
  `catalogue_sale_code` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

ALTER TABLE `vouchers`
  ADD UNIQUE KEY `voucher_code` (`voucher_code`);

ALTER TABLE `vouchers_items`
  ADD KEY `voucher_code` (`voucher_code`);

ALTER TABLE `catalogue_items`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `catalogue_items` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `catalogue_packages`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `catalogue_packages` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `catalogue_pages`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `catalogue_pages` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `external_texts`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `external_texts` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `games_maps`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `games_maps` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `games_player_spawns`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `games_player_spawns` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `games_ranks`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `games_ranks` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `housekeeping_audit_log`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `housekeeping_audit_log` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_definitions`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_definitions` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_moodlight_presets`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_moodlight_presets` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_pets`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_pets` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_photos`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_photos` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_teleporter_links`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `items_teleporter_links` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `messenger_friends`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `messenger_friends` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `messenger_messages`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `messenger_messages` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `messenger_requests`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `messenger_requests` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `public_items`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `public_items` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rank_badges`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rank_badges` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rank_fuserights`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rank_fuserights` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rare_cycle`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rare_cycle` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_bots`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_bots` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_categories`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_categories` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_events`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_events` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_models`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_models` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_rights`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `rooms_rights` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `room_chatlogs`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `room_chatlogs` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `schema_migrations`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `schema_migrations` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `settings`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `settings` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `soundmachine_disks`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `soundmachine_disks` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `soundmachine_playlists`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `soundmachine_playlists` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `soundmachine_songs`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `soundmachine_songs` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `soundmachine_tracks`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `soundmachine_tracks` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_badges`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_badges` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_bans`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_bans` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_club_gifts`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_club_gifts` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_ip_logs`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_ip_logs` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_mutes`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_mutes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_room_favourites`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_room_favourites` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_room_votes`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `users_room_votes` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `vouchers`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `vouchers` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `vouchers_history`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `vouchers_history` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `vouchers_items`DEFAULT  CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE `vouchers_items` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

DELETE FROM catalogue_pages WHERE layout = 'ctlg_recycler';
INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `name_index`, `name`, `layout`, `image_headline`) VALUES ('91', '13', '1', 'Ecotron', 'Ecotron', 'ctlg_recycler', 'catalog_recycler_headline1'); 
