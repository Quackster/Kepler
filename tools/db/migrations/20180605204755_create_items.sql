-- migrate:up
CREATE TABLE `items` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `room_id` int(11) DEFAULT 0,
  `definition_id` int(11) NOT NULL,
  `x` varchar(255) DEFAULT '0',
  `y` varchar(255) DEFAULT '0',
  `z` varchar(255) DEFAULT '0',
  `wall_position` varchar(255) NOT NULL DEFAULT '',
  `rotation` int(11) DEFAULT 0,
  `custom_data` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `items`;
