CREATE TABLE `room_chatlogs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `chat_type` tinyint(1) NOT NULL,
  `message` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `room_chatlogs`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `room_chatlogs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
  
 CREATE TABLE `users_bans` (
  `ban_type` enum('MACHINE_ID','IP_ADDRESS','USER_ID') NOT NULL,
  `banned_value` varchar(250) NOT NULL,
  `banned_message` text NOT NULL DEFAULT '',
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