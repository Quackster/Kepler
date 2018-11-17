-- migrate:up
CREATE TABLE `users_room_votes` (
  `user_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `vote` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `users_room_votes`;
