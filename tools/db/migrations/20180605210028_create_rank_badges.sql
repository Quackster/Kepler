-- migrate:up
CREATE TABLE `rank_badges` (
  `rank` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `badge` char(3) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `rank_badges`;
