-- migrate:up
ALTER TABLE `catalogue_pages` CHANGE `body` `body` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '';

ALTER TABLE `catalogue_pages`
	ADD COLUMN `index_visible` TINYINT(1) NOT NULL DEFAULT '1' AFTER `min_role`;

ALTER TABLE `catalogue_pages`
	ADD COLUMN `link_list` VARCHAR(255) NOT NULL DEFAULT '' AFTER `name_index`;
    
INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `index_visible`, `name_index`, `link_list`, `name`, `layout`, `image_headline`, `image_teasers`, `body`, `label_pick`, `label_extra_s`, `label_extra_t`) VALUES (41, 3, 1, 1, 'Camera', 'Camera2', 'Camera', 'ctlg_camera1', 'catalog_camera_headline1', 'campic_cam,campic_film,', 'With your Camera you can take pictures of just about anything in the hotel - your friend on the loo (hehe), your best dive in the Lido, or your room when you\'ve got it just right!<br><br>A camera costs 10 Credits (two free photos included).', NULL, NULL, '1:When you\'ve used your free photos, you\'ll need to buy more. Each roll of film takes five photos. Your Camera will show how much film you have left and loads the next roll automatically.<br><br>Each Film (5 photos) costs:');

INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `index_visible`, `name_index`, `link_list`, `name`, `layout`, `image_headline`, `image_teasers`, `body`, `label_pick`, `label_extra_s`, `label_extra_t`) VALUES (42, 3, 1, 0, 'Camera2', '', 'Camera', 'ctlg_camera2', 'catalog_camera_headline1', 'campic_help,', 'CAMERA FUNCTIONS<br><br>1. Press this button to take a photo.<br>2. Photo cancel - for when you\'ve chopped off your friend\'s head!<br>3. Zoom in and out.<br>4. Photo counter - shows how much film you have left<br>5. Caption Box - write your caption before saving the photo.<br>6. Save - this moves the photo to your giant.<br>You can give photos to your friends, or put them on the wall like posters.', NULL, NULL, NULL);

UPDATE `catalogue_pages` SET `index_visible`='0' WHERE  `id`=42;

INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `definition_id`, `item_specialspriteid`, `package_name`, `package_description`, `is_package`) VALUES (513, 'camera', 41, 0, 10, 421, 0, NULL, NULL, 0);
INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `definition_id`, `item_specialspriteid`, `package_name`, `package_description`, `is_package`) VALUES (514, 'film', 41, 1, 6, 423, 0, NULL, NULL, 0);

INSERT INTO `items_definitions` (`id`, `sprite`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES (421, 'camera', '', 1, 1, 0, 'solid');
INSERT INTO `items_definitions` (`id`, `sprite`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES (422, 'photo', '', 0, 0, 0, 'photo,wall_item');
INSERT INTO `items_definitions` (`id`, `sprite`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES (423, 'film', '', 0, 0, 0, '');

CREATE TABLE IF NOT EXISTS `items_photos` (
  `photo_id` int(11) NOT NULL,
  `photo_user_id` bigint(11) NOT NULL,
  `timestamp` bigint(11) NOT NULL,
  `photo_data` blob NOT NULL,
  `photo_checksum` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `items_photos`
	CHANGE COLUMN `photo_id` `photo_id` INT(11) NOT NULL FIRST,
	ADD PRIMARY KEY (`photo_id`),
	ADD UNIQUE INDEX `photo_id` (`photo_id`);

-- migrate:down

