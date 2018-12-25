TRUNCATE `users_room_votes`;

ALTER TABLE `users_room_votes` ADD `expire_time` BIGINT(11) NOT NULL DEFAULT '0' AFTER `vote`;
ALTER TABLE `rooms` ADD `rating` INT(11) NOT NULL DEFAULT '0' AFTER `visitors_max`;

INSERT INTO `items_definitions` (`id`, `sprite`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES (NULL, 'hc_crpt', '0,0,0', '3', '5', '0', 'can_stand_on_top');

INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `is_hidden`, `price`, `definition_id`, `item_specialspriteid`, `package_name`, `package_description`, `is_package`) VALUES (NULL, 'hc_crpt', '27', '14', '0', '5', '761', '0', NULL, NULL, '0');
