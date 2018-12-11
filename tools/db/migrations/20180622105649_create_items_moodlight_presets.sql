-- migrate:up
CREATE TABLE `items_moodlight_presets` (
  `item_id` int(11) NOT NULL,
  `current_preset` int(11) NOT NULL DEFAULT 1,
  `preset_1` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1,#000000,255',
  `preset_2` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1,#000000,255',
  `preset_3` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1,#000000,255'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


ALTER TABLE `items_moodlight_presets`
  ADD PRIMARY KEY (`item_id`);

-- migrate:down
DROP TABLE `items_moodlight_presets`;