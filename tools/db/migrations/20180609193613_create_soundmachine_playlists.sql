-- migrate:up
CREATE TABLE `soundmachine_playlists` (
  `item_id` int(11) NOT NULL,
  `song_id` int(11) NOT NULL,
  `slot_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



-- migrate:down
DROP TABLE `soundmachine_playlists`;
