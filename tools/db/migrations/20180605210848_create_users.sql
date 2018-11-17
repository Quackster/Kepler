-- migrate:up
CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `figure` varchar(255) NOT NULL,
  `pool_figure` varchar(255) NOT NULL,
  `sex` char(1) NOT NULL DEFAULT 'M',
  `motto` varchar(100) NOT NULL DEFAULT 'de kepler whey',
  `credits` int(11) NOT NULL DEFAULT 200,
  `tickets` int(11) NOT NULL DEFAULT 0,
  `film` int(11) NOT NULL DEFAULT 0,
  `rank` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `console_motto` varchar(100) NOT NULL DEFAULT 'I''m a new user!',
  `last_online` int(11) NOT NULL DEFAULT 0,
  `sso_ticket` varchar(255) NOT NULL,
  `club_subscribed` bigint(11) NOT NULL DEFAULT 0,
  `club_expiration` bigint(11) NOT NULL DEFAULT 0,
  `badge` char(3) NOT NULL DEFAULT '',
  `badge_active` tinyint(1) NOT NULL DEFAULT 1,
  `allow_stalking` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `users`;
