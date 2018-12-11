-- migrate:up
CREATE TABLE `messenger_messages` (
  `id` int(11) NOT NULL,
  `receiver_id` int(11) DEFAULT NULL,
  `sender_id` int(11) DEFAULT NULL,
  `unread` varchar(255) DEFAULT NULL,
  `body` varchar(255) DEFAULT NULL,
  `date` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `messenger_messages`;
