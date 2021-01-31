ALTER TABLE `rooms`
	ADD COLUMN `is_hidden` TINYINT(1) NOT NULL DEFAULT 0 AFTER `rating`;

REPLACE INTO `rooms` (`id`, `owner_id`, `category`, `name`, `description`, `model`, `ccts`, `wallpaper`, `floor`, `showname`, `superusers`, `accesstype`, `password`, `visitors_now`, `visitors_max`, `rating`, `is_hidden`, `created_at`, `updated_at`) VALUES
	(1, '0', 3, 'Welcome Lounge', 'welcome_lounge', 'newbie_lobby', 'hh_room_nlobby', 0, 0, 0, 0, 0, '', 0, 40, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:35:48'),
	(2, '0', 3, 'Theatredome', 'theatredrome', 'theater', 'hh_room_theater', 0, 0, 0, 0, 0, '', 0, 100, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:30:08'),
	(3, '0', 3, 'Library', 'library', 'library', 'hh_room_library', 0, 0, 0, 0, 0, '', 0, 30, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:33:57'),
	(4, '0', 5, 'TV Studio', 'tv_studio', 'tv_studio', 'hh_room_tv_studio_general', 0, 0, 0, 0, 0, '', 0, 20, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(5, '0', 5, 'Cinema', 'habbo_cinema', 'cinema_a', 'hh_room_cinema', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:07:00'),
	(6, '0', 5, 'Power Gym', 'sport', 'sport', 'hh_room_sport', 0, 0, 0, 0, 0, '', 0, 35, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(7, '0', 5, 'Olympic Stadium', 'ballroom', 'ballroom', 'hh_room_ballroom', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(8, '0', 5, 'Habbo Kitchen', 'hotel_kitchen', 'cr_kitchen', 'hh_room_kitchen', 0, 0, 0, 0, 0, '', 0, 20, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:07:35'),
	(9, '0', 6, 'The Dirty Duck Pub', 'the_dirty_duck_pub', 'pub_a', 'hh_room_pub', 0, 0, 0, 0, 0, '', 0, 40, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(10, '0', 6, 'Cafe Ole', 'cafe_ole', 'cafe_ole', 'hh_room_cafe', 0, 0, 0, 0, 0, '', 0, 35, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(11, '0', 6, 'Gallery Cafe', 'eric\'s_eaterie', 'cr_cafe', 'hh_room_erics', 0, 0, 0, 0, 0, '', 0, 35, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(12, '0', 6, 'Space Cafe', 'space_cafe', 'space_cafe', 'hh_room_space_cafe', 0, 0, 0, 0, 0, '', 0, 35, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(13, '0', 7, 'Rooftop Terrace', 'rooftop', 'rooftop', 'hh_room_rooftop', 0, 0, 0, 0, 0, '', 0, 30, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(14, '0', 7, 'Rooftop Cafe', 'rooftop', 'rooftop_2', 'hh_room_rooftop', 0, 0, 0, 0, 0, '', 0, 20, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:51'),
	(15, '0', 6, 'Palazzo Pizza', 'pizza', 'pizza', 'hh_room_pizza', 0, 0, 0, 0, 0, '', 0, 40, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(16, '0', 6, 'Habburgers', 'habburger\'s', 'habburger', 'hh_room_habburger', 0, 0, 0, 0, 0, '', 0, 40, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(17, '0', 8, 'Grandfathers Lounge', 'dusty_lounge', 'dusty_lounge', 'hh_room_dustylounge', 0, 0, 0, 0, 0, '', 0, 45, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(18, '0', 7, 'Oriental Tearoom', 'tearoom', 'tearoom', 'hh_room_tearoom', 0, 0, 0, 0, 0, '', 0, 40, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(19, '0', 7, 'Oldskool Lounge', 'old_skool', 'old_skool0', 'hh_room_old_skool', 0, 0, 0, 0, 0, '', 0, 45, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(20, '0', 7, 'Oldskool Dancefloor', 'old_skool', 'old_skool1', 'hh_room_old_skool', 0, 0, 0, 0, 0, '', 0, 45, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:51'),
	(21, '0', 7, 'The Chromide Club', 'the_chromide_club', 'malja_bar_a', 'hh_room_disco', 0, 0, 0, 0, 0, '', 0, 45, 0, 0, '2018-08-11 07:54:01', '2021-01-31 14:04:47'),
	(22, '0', 7, 'The Chromide Club II', 'the_chromide_club', 'malja_bar_b', 'hh_room_disco', 0, 0, 0, 0, 0, '', 0, 50, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:04:41'),
	(23, '0', 7, 'Club Massiva', 'club_massiva', 'bar_a', 'hh_room_bar', 0, 0, 0, 0, 0, '', 0, 45, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(24, '0', 7, 'Club Massiva II', 'club_massiva2', 'bar_b', 'hh_room_bar', 0, 0, 0, 0, 0, '', 0, 70, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:51'),
	(25, '0', 6, 'Sunset Cafe', 'sunset_cafe', 'sunset_cafe', 'hh_room_sunsetcafe', 0, 0, 0, 0, 0, '', 0, 35, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(26, '0', 7, 'Oasis Spa', 'cafe_gold', 'cafe_gold0', 'hh_room_gold', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(27, '0', 9, 'Treehugger Garden', 'chill', 'chill', 'hh_room_chill', 0, 0, 0, 0, 0, '', 0, 30, 0, 0, '2018-08-11 07:54:01', '2019-12-05 01:05:25'),
	(28, '0', 8, 'Club Mammoth', 'club_mammoth', 'club_mammoth', 'hh_room_clubmammoth', 0, 0, 0, 0, 0, '', 0, 45, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(29, '0', 9, 'Floating Garden', 'floatinggarden', 'floatinggarden', 'hh_room_floatinggarden', 0, 0, 0, 0, 0, '', 0, 80, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(30, '0', 9, 'Picnic Fields', 'picnic', 'picnic', 'hh_room_picnic', 0, 0, 0, 0, 0, '', 0, 55, 0, 0, '2018-08-11 07:54:01', '2021-01-31 14:15:27'),
	(31, '0', 9, 'Sun Terrace', 'sun_terrace', 'sun_terrace', 'hh_room_sun_terrace', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:21:54'),
	(32, '0', 9, 'Peaceful Park', 'gate_park', 'gate_park', 'hh_room_gate_park', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2018-11-17 00:14:57'),
	(33, '0', 9, 'Peaceful Park B', 'gate_park', 'gate_park_2', 'hh_room_gate_park', 0, 0, 0, 0, 0, '', 0, 50, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:51'),
	(34, '0', 9, 'The Park', 'park', 'park_a', 'hh_room_park_general,hh_room_park', 0, 0, 0, 0, 0, '', 0, 45, 0, 0, '2018-08-11 07:54:01', '2021-01-23 17:10:25'),
	(35, '0', 9, 'The Infobus', 'park', 'park_b', 'hh_room_park_general,hh_room_park', 0, 0, 0, 0, 0, '', 0, 20, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:05:51'),
	(36, '0', 10, 'Habbo Lido', 'habbo_lido', 'pool_a', 'hh_room_pool,hh_people_pool', 0, 0, 0, 0, 0, '', 0, 60, 0, 0, '2018-08-11 07:54:01', '2021-01-31 14:14:03'),
	(37, '0', 10, 'Lido B', 'habbo_lido_ii', 'pool_b', 'hh_room_pool,hh_people_pool', 0, 0, 0, 0, 0, '', 1, 60, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:14:03'),
	(38, '0', 10, 'Rooftop Rumble', 'rooftop_rumble', 'md_a', 'hh_room_terrace,hh_paalu,hh_people_pool,hh_people_paalu', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2021-01-23 17:10:01'),
	(39, '0', 11, 'Main Lobby', 'main_lobby', 'lobby_a', 'hh_room_lobby', 0, 0, 0, 0, 0, '', 0, 100, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(40, '0', 11, 'Basement Lobby', 'basement_lobby', 'floorlobby_a', 'hh_room_floorlobbies', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(41, '0', 11, 'Median Lobby', 'median_lobby', 'floorlobby_b', 'hh_room_floorlobbies', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:06:33'),
	(42, '0', 11, 'Skylight Lobby', 'skylight_lobby', 'floorlobby_c', 'hh_room_floorlobbies', 0, 0, 0, 0, 0, '', 0, 50, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:06:42'),
	(43, '0', 6, 'Ice Cafe', 'ice_cafe', 'ice_cafe', 'hh_room_icecafe', 0, 0, 0, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(44, '0', 6, 'Net Cafe', 'netcafe', 'netcafe', 'hh_room_netcafe', 0, 0, 0, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(45, '0', 5, 'Beauty Salon', 'beauty_salon_loreal', 'beauty_salon0', 'hh_room_beauty_salon_general', 0, 0, 0, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2021-01-31 14:15:42'),
	(46, '0', 5, 'The Den', 'the_den', 'cr_staff', 'hh_room_den', 0, 0, 0, 0, 0, '', 0, 100, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(47, '0', 12, 'Lower Hallways', 'hallway', 'hallway2', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(48, '0', 12, 'Lower Hallways I', 'hallway', 'hallway0', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(49, '0', 12, 'Lower Hallways II', 'hallway', 'hallway1', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(50, '0', 12, 'Lower Hallways III', 'hallway', 'hallway3', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(51, '0', 12, 'Lower Hallways IV', 'hallway', 'hallway5', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(52, '0', 12, 'Lower Hallways V', 'hallway', 'hallway4', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(53, '0', 12, 'Upper Hallways', 'hallway_ii', 'hallway9', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2018-08-11 07:54:01'),
	(54, '0', 12, 'Upper Hallways I', 'hallway_ii', 'hallway8', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(55, '0', 12, 'Upper Hallways II', 'hallway_ii', 'hallway7', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(56, '0', 12, 'Upper Hallways III', 'hallway_ii', 'hallway6', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(57, '0', 12, 'Upper Hallways IV', 'hallway_ii', 'hallway10', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(58, '0', 12, 'Upper Hallways V', 'hallway_ii', 'hallway11', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(59, '0', 7, 'Star Lounge', 'star_lounge', 'star_lounge', 'hh_room_starlounge', 0, 0, 1, 0, 0, '', 0, 35, 0, 0, '2018-08-11 07:54:01', '2019-10-15 20:45:32'),
	(60, '0', 8, 'Club Orient', 'orient', 'orient', 'hh_room_orient', 0, 0, 1, 0, 0, '', 0, 35, 0, 0, '2018-08-11 07:54:01', '2019-10-16 23:30:17'),
	(61, '0', 13, 'Cunning Fox Gamehall', 'cunning_fox_gamehall', 'entryhall', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2021-01-23 17:39:36'),
	(62, '0', 13, 'TicTacToe hall', 'cunning_fox_gamehall/1', 'hallA', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(63, '0', 13, 'Battleships hall', 'cunning_fox_gamehall/2', 'hallB', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(64, '0', 13, 'Chess hall', 'cunning_fox_gamehall/3', 'hallC', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(65, '0', 13, 'Poker hall', 'cunning_fox_gamehall/4', 'hallD', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:03:52'),
	(66, '0', 13, 'Battleball Lobby', 'bb_lobby_beginner_0', 'bb_lobby_1', 'hh_game_bb,hh_game_bb_room,hh_game_bb_ui,hh_gamesys', 0, 0, 1, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2021-01-23 17:39:26'),
	(67, '0', 13, 'Snowstorm Lobby', 'sw_lobby_beginner_0', 'snowwar_lobby_1', 'hh_gamesys,hh_game_snowwar,hh_game_snowwar_room,hh_game_snowwar_ui', 0, 0, 1, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2021-01-23 17:38:51'),
	(68, '0', 5, 'Imperial Palace', 'emperors', 'emperors', 'hh_room_emperors', 0, 0, 0, 0, 0, '', 0, 25, 0, 0, '2018-08-11 07:54:01', '2020-12-09 20:40:49'),
	(69, '0', 5, 'Beauty Salon II', 'beauty_salon_loreal', 'beauty_salon1', 'hh_room_beauty_salon_general', 0, 0, 0, 0, 0, '', 1, 30, 0, 1, '2018-08-11 07:54:01', '2021-01-31 14:15:42');

UPDATE rooms SET is_hidden = 1 WHERE id = 14;
UPDATE rooms SET is_hidden = 1 WHERE id = 20;
UPDATE rooms SET is_hidden = 1 WHERE id = 22;
UPDATE rooms SET is_hidden = 1 WHERE id = 24;
UPDATE rooms SET is_hidden = 1 WHERE id = 33;
UPDATE rooms SET is_hidden = 1 WHERE id = 35;
UPDATE rooms SET is_hidden = 1 WHERE id = 37;
UPDATE rooms SET is_hidden = 1 WHERE id = 48;
UPDATE rooms SET is_hidden = 1 WHERE id = 49;
UPDATE rooms SET is_hidden = 1 WHERE id = 50;
UPDATE rooms SET is_hidden = 1 WHERE id = 51;
UPDATE rooms SET is_hidden = 1 WHERE id = 52;
UPDATE rooms SET is_hidden = 1 WHERE id = 54;
UPDATE rooms SET is_hidden = 1 WHERE id = 55;
UPDATE rooms SET is_hidden = 1 WHERE id = 56;
UPDATE rooms SET is_hidden = 1 WHERE id = 57;
UPDATE rooms SET is_hidden = 1 WHERE id = 58;
UPDATE rooms SET is_hidden = 1 WHERE id = 62;
UPDATE rooms SET is_hidden = 1 WHERE id = 63;
UPDATE rooms SET is_hidden = 1 WHERE id = 64;
UPDATE rooms SET is_hidden = 1 WHERE id = 65;
UPDATE rooms SET is_hidden = 1 WHERE id = 69;

DROP TABLE IF EXISTS `public_roomwalkways`;
CREATE TABLE IF NOT EXISTS `public_roomwalkways` (
  `room_id` int(11) DEFAULT NULL,
  `to_id` int(1) DEFAULT NULL,
  `coords_map` varchar(255) DEFAULT NULL,
  `door_position` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELETE FROM `public_roomwalkways`;
INSERT INTO `public_roomwalkways` (`room_id`, `to_id`, `coords_map`, `door_position`) VALUES
	(45, 69, '20,23 20,24 20,25 21,23 21,24 21,25', '3,23,0,2'),
	(69, 45, '0,22 1,23', '19,24,0,6'),
	(34, 35, '28,4', NULL),
	(35, 34, '11,2', '28,5,0,4'),
	(32, 33, '23,0 22,0 20,0 19,0 18,0 17,0 16,0 15,0 14,0 11,0 10,0 9,0', NULL),
	(33, 32, '16,24 15,24 17,24 18,24 18,25 17,25 16,25 15,25 18,26 17,26 16,26', '16,2,2,4'),
	(13, 14, '9,4 10,4 9,3', NULL),
	(14, 13, '3,11 4,11 5,11', '10,5,4,4'),
	(19, 20, '16,18', NULL),
	(20, 19, '0,7', '15,18,0,6'),
	(21, 22, '14,0 15,0', NULL),
	(22, 21, '5,25 ', '15,1,4,4'),
	(23, 24, '9,32 10,32 11,32 9,33 10,33', NULL),
	(24, 23, '1,10 1,11 1,12', '10,30,5,0'),
	(36, 37, '19,3 20,4 21,5 22,6 23,7 24,8 25,9 26,10 27,11 28,12', NULL),
	(36, 37, '30,14 31,15 32,16 33,17 34,18 35,19 36,20 37,21 38,22 39,23', '18,30,1,1'),
	(37, 36, '13,26 14,27 15,28 16,29 17,30 18,31 19,32 20,33 21,34', '34,19,1,5'),
	(47, 48, '0,6 0,7 0,8 0,9', '29,3,1,6'),
	(47, 50, '6,23 7,23 8,23 9,23', '7,2,1,4'),
	(47, 52, '27,6 27,7 27,8 27,9', '2,3,0,2'),
	(48, 47, '31,5 31,4 31,3 31,2', '2,7,1,2'),
	(48, 49, '14,19 15,19 16,19 17,19', '15,2,0,4'),
	(49, 50, '31,9 31,8 31,7 31,6', '2,8,1,2'),
	(49, 48, '17,0 16,0 15,0 14,0', '16,17,1,0'),
	(50, 47, '9,0 8,0 7,0 6,0', '8,21,1,0'),
	(50, 49, '0,9 0,8 0,7 0,6', '29,7,0,6'),
	(50, 51, '31,6 31,7 31,8 31,9', '2,15,0,2'),
	(51, 50, '0,17 0,16 0,15 0,14', '29,7,0,6'),
	(51, 52, '22,0 23,0 24,0 25,0', '24,17,1,0'),
	(52, 47, '0,2 0,3 0,4 0,5', '25,7,0,6'),
	(52, 51, '22,19 23,19 24,19 25,19', '24,2,1,4'),
	(53, 54, '14,0 15,0 16,0 17,0', '19,21,0,0'),
	(53, 57, '14,31 15,31 16,31 17,31', '3,6,1,4'),
	(53, 55, '0,14 0,15 0,16 0,17', '17,23,0,6'),
	(53, 58, '31,17 31,16 31,15 31,14', '2,3,1,2'),
	(54, 55, '0,14 0,15 0,16 0,17', '13,8,1,6'),
	(54, 53, '18,23 19,23 20,23 21,23', '16,2,0,4'),
	(55, 54, '15,6 15,7 15,8 15,9', '2,15,1,2'),
	(55, 56, '0,25 0,24 0,23 0,22', '21,12,0,6'),
	(55, 53, '19,22 19,23 19,24 19,25', '2,15,0,2'),
	(56, 55, '23,13 23,12 23,11 23,10', '2,23,0,2'),
	(57, 53, '2,4 3,4 4,4 5,4', '15,29,0,0'),
	(57, 58, '17,0 17,1 17,2 17,3', '10,19,0,2'),
	(58, 57, '8,18 8,19 8,20 8,21', '15,1,0,6'),
	(58, 53, '0,5 0,4 0,3 0,2', '29,15,0,6'),
	(61, 62, '2,0 3,0', '1,1,1,4'),
	(61, 63, '8,0 9,0', '2,1,1,4'),
	(61, 64, '14,0 15,0', '1,1,1,4'),
	(61, 65, '0,2 0,3', '1,1,1,4'),
	(62, 61, '0,0 1,0', '3,1,1,4'),
	(63, 61, '2,0 1,0', '9,1,1,4'),
	(64, 61, '0,0 1,0', '15,1,1,4'),
	(65, 61, '0,0 1,0', '1,3,1,2'),
	(37, 36, '0,13 1,14 2,15 3,16 4,17 5,18 6,19 7,20 8,21 9,22 10,23 11,24 12,25', '23,7,7,5');
