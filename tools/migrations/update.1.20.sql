CREATE TABLE `room_chatlogs` (
  `user_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `chat_type` tinyint(1) NOT NULL,
  `message` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
  
CREATE TABLE `users_bans` (
  `ban_type` enum('MACHINE_ID','IP_ADDRESS','USER_ID') NOT NULL,
  `banned_value` varchar(250) NOT NULL,
  `message` text NOT NULL,
  `banned_until` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `users_bans`
  ADD PRIMARY KEY (`banned_value`);
  
CREATE TABLE `rooms_events` (
  `room_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `expire_time` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `rooms_events`
  ADD PRIMARY KEY (`room_id`),
  ADD UNIQUE KEY `room_id` (`room_id`);
  
CREATE TABLE `users_ip_logs` (
  `user_id` int(11) NOT NULL,
  `ip_address` varchar(256) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `rooms_models` CHANGE `trigger_class` `trigger_class` ENUM('flat_trigger','battleball_lobby_trigger','snowstorm_lobby_trigger','space_cafe_trigger','habbo_lido_trigger','rooftop_rumble_trigger','diving_deck_trigger','infobus_park','infobus_poll','none') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'flat_trigger';

UPDATE `rooms_models` SET trigger_class = 'infobus_park' WHERE `model_id` = 'park_a';
UPDATE `rooms_models` SET trigger_class = 'infobus_poll' WHERE `model_id` = 'park_b';

CREATE TABLE `rooms_bots` (
  `id` int(11) NOT NULL,
  `name` varchar(25) NOT NULL,
  `mission` varchar(255) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `start_look` varchar(25) NOT NULL,
  `figure` varchar(255) NOT NULL,
  `walkspace` text NOT NULL,
  `room_id` int(11) NOT NULL DEFAULT 0,
  `speech` mediumtext NOT NULL,
  `response` mediumtext NOT NULL,
  `unrecognised_response` mediumtext NOT NULL,
  `hand_items` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `rooms_bots`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `rooms_bots`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=46;
  
CREATE TABLE `items_pets` (
  `id` int(11) NOT NULL,
  `item_id` bigint(11) NOT NULL,
  `name` varchar(15) NOT NULL,
  `type` varchar(1) NOT NULL,
  `race` int(3) NOT NULL,
  `colour` varchar(6) NOT NULL,
  `nature_positive` int(3) NOT NULL,
  `nature_negative` int(3) NOT NULL,
  `friendship` float NOT NULL DEFAULT 1,
  `born` bigint(11) NOT NULL,
  `last_kip` bigint(11) NOT NULL,
  `last_eat` bigint(11) NOT NULL,
  `last_drink` bigint(11) NOT NULL,
  `last_playtoy` bigint(11) NOT NULL,
  `last_playuser` bigint(11) NOT NULL,
  `x` int(3) NOT NULL DEFAULT 0,
  `y` int(3) NOT NULL DEFAULT 0,
  `rotation` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `items_pets`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`);

ALTER TABLE `items_pets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
  
INSERT INTO `items_definitions` (`id`, `sprite`, `sprite_id`, `name`, `description`, `colour`, `length`, `width`, `top_height`, `max_status`, `behaviour`, `interactor`, `is_tradable`, `is_recyclable`, `drink_ids`) VALUES (NULL, 'nest', '34', '', '', '0,0,0', '1', '1', '0', '2', 'can_stand_on_top,requires_rights_for_interaction', 'pet_nest', '1', '1', '');

-- Needed for fixing jukebox disks
UPDATE items SET is_hidden = 0 WHERE definition_id = 1412;

ALTER TABLE `users` CHANGE `sso_ticket` `sso_ticket` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL;