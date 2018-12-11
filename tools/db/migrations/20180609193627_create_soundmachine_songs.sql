-- migrate:up
CREATE TABLE `soundmachine_songs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `item_id` int(11) NOT NULL,
  `length` int(3) NOT NULL DEFAULT 0,
  `data` text COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `burnt` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `soundmachine_songs`;
