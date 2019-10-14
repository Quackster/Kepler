DROP TABLE IF EXISTS `recycler_rewards`;
DROP TABLE IF EXISTS `recycler_sessions`;
DELETE FROM catalogue_pages WHERE layout = 'ctlg_recycler';
DELETE FROM catalogue_pages WHERE name = 'Recycler Furni';

CREATE TABLE IF NOT EXISTS `recycler_rewards` (
  `id` int(11) NOT NULL,
  `sale_code` varchar(255) NOT NULL,
  `item_cost` int(11) NOT NULL DEFAULT 10,
  `recycling_session_time_seconds` int(11) DEFAULT 0,
  `collection_time_seconds` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `recycler_rewards` (`id`, `sale_code`, `item_cost`, `recycling_session_time_seconds`, `collection_time_seconds`) VALUES
	(2, 'rclr_garden', 30, 7200, 1800),
	(3, 'rclr_sofa', 50, 10800, 1800),
	(1, 'rclr_chair', 20, 3600, 1800);

CREATE TABLE IF NOT EXISTS `recycler_sessions` (
  `user_id` int(11) NOT NULL,
  `reward_id` int(11) NOT NULL,
  `session_started` datetime NOT NULL DEFAULT current_timestamp(),
  `items` text NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

UPDATE items_definitions SET is_recyclable = 0 WHERE id = 415;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 416;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 417;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 418;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 419;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 420;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 422;
UPDATE items_definitions SET is_recyclable = 0 WHERE id = 1412;
	
INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `index_visible`, `is_club_only`, `name_index`, `link_list`, `name`, `layout`, `image_headline`, `image_teasers`, `body`, `label_pick`, `label_extra_s`, `label_extra_t`) VALUES (91, 13, 1, 1, 0, 'Recycler', '', 'Recycler', 'ctlg_recycler', 'catalog_recycler_headline1', '', '', NULL, NULL, NULL);
INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `index_visible`, `is_club_only`, `name_index`, `link_list`, `name`, `layout`, `image_headline`, `image_teasers`, `body`, `label_pick`, `label_extra_s`, `label_extra_t`) VALUES (92, 116, 5, 1, 0, 'Recycler Furni', '', 'Recycler Furni', 'ctlg_layout2', 'catalog_rares_headline1', '', 'Yet another special page.', 'Click on the item you want for more information', NULL, NULL);

INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `is_hidden`, `amount`, `definition_id`, `item_specialspriteid`, `name`, `description`, `is_package`, `package_name`, `package_description`) VALUES (1348, 'rclr_chair', '92', 9, 1, 0, 1, 1414, 0, 'Palm Chair', 'Watch out for coconuts', 0, NULL, NULL);
INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `is_hidden`, `amount`, `definition_id`, `item_specialspriteid`, `name`, `description`, `is_package`, `package_name`, `package_description`) VALUES (1349, 'rclr_garden', '92', 9, 1, 0, 1, 1415, 0, 'Water Garden', 'Self watering', 0, NULL, NULL);
INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `is_hidden`, `amount`, `definition_id`, `item_specialspriteid`, `name`, `description`, `is_package`, `package_name`, `package_description`) VALUES (1350, 'rclr_sofa', '92', 9, 1, 0, 1, 1416, 0, 'Polar Sofa', 'Snuggle up together', 0, NULL, NULL);

INSERT INTO `items_definitions` (`id`, `sprite`, `sprite_id`, `name`, `description`, `colour`, `length`, `width`, `top_height`, `max_status`, `behaviour`, `interactor`, `is_tradable`, `is_recyclable`, `drink_ids`) VALUES (1414, 'rclr_chair', -1, 'Palm Chair', 'Watch out for coconuts', '0,0,0', 1, 1, 1, '2', 'can_sit_on_top', 'chair', 1, 0, '');
INSERT INTO `items_definitions` (`id`, `sprite`, `sprite_id`, `name`, `description`, `colour`, `length`, `width`, `top_height`, `max_status`, `behaviour`, `interactor`, `is_tradable`, `is_recyclable`, `drink_ids`) VALUES (1415, 'rclr_garden', -1, 'Water Garden', 'Self watering', '0,0,0', 1, 3, 0, '2', 'solid', 'default', 1, 0, '');
INSERT INTO `items_definitions` (`id`, `sprite`, `sprite_id`, `name`, `description`, `colour`, `length`, `width`, `top_height`, `max_status`, `behaviour`, `interactor`, `is_tradable`, `is_recyclable`, `drink_ids`) VALUES (1416, 'rclr_sofa', -1, 'Polar Sofa', 'Snuggle up together', '0,0,0', 2, 1, 1, '0', 'can_sit_on_top', 'chair', 1, 0, '');



