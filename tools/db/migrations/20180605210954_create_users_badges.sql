-- migrate:up
CREATE TABLE `users_badges` (
  `user_id` int(11) NOT NULL,
  `badge` char(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `users_badges`;
