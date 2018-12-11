-- migrate:up
CREATE TABLE `catalogue_packages` (
  `salecode` varchar(255) DEFAULT NULL,
  `definition_id` int(11) DEFAULT NULL,
  `special_sprite_id` int(11) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `catalogue_packages` (`salecode`, `definition_id`, `special_sprite_id`, `amount`) VALUES
('deal_rollers_red_5', 184, 0, 5),
('deal_rollers_red_3', 184, 0, 3),
('deal_rollers_blue_5', 180, 0, 5),
('deal_rollers_blue_3', 180, 0, 3),
('deal_rollers_green_5', 181, 0, 5),
('deal_rollers_green_3', 181, 0, 3),
('deal_rollers_navy_5', 182, 0, 5),
('deal_rollers_navy_3', 182, 0, 3),
('deal_rollers_purple_5', 183, 0, 5),
('deal_rollers_purple_3', 183, 0, 3),
('deal_dogfood', 155, 0, 6),
('deal_catfood', 156, 0, 6),
('deal_crocfood', 236, 0, 6),
('deal_cabbage', 157, 0, 6),
('deal_soundmachine1', 232, 0, 1),
('deal_soundmachine1', 239, 0, 1),
('deal_hcrollers', 226, 0, 5),
('deal_throne', 107, 0, 10);

-- migrate:down
DROP TABLE `catalogue_packages`;
