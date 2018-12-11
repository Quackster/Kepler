-- migrate:up
CREATE TABLE `soundmachine_tracks` (
  `soundmachine_id` int(11) NOT NULL,
  `track_id` int(11) NOT NULL,
  `slot_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- migrate:down
DROP TABLE `soundmachine_tracks`;
