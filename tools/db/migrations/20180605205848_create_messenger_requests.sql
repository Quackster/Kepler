-- migrate:up
CREATE TABLE `messenger_requests` (
  `from_id` int(11) DEFAULT NULL,
  `to_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `messenger_requests`;
