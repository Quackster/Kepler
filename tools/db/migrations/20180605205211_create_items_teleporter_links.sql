-- migrate:up
CREATE TABLE `items_teleporter_links` (
  `item_id` int(11) NOT NULL,
  `linked_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `items_teleporter_links`;
