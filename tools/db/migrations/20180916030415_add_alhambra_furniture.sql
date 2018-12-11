-- migrate:up
INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `index_visible`, `name_index`, `link_list`, `name`, `layout`, `image_headline`, `image_teasers`, `body`, `label_pick`, `label_extra_s`, `label_extra_t`) VALUES
(46, 28, 1, 1, 'Alhambra', '', 'Alhambra', 'ctlg_layout2', 'catalog_alh_headline2', 'catalog_alh_teaser2,', 'The Palace of Alhambra has appeared and with it this exotic and beautifully crafted range of Arabian Furni. Luxury seating and gourmet food combine to make your room sparkle with riches.', 'Click on the item you want for more information', 's:2:Get your Alhambrian goodies now!', '');

INSERT INTO `items_definitions` (`id`, `sprite`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES
(745, 'arabian_bigtb', '0,0,0', 3, 2, 1, 'solid,can_stack_on_top'),
(746, 'arabian_chair', '0,0,0', 1, 1, 1, 'can_sit_on_top'),
(747, 'arabian_divdr', '0,0,0', 1, 2, 0, 'solid'),
(748, 'arabian_pllw', '0,0,0', 1, 1, 1, 'can_sit_on_top'),
(749, 'arabian_rug', '0,0,0', 3, 5, 0.1, 'can_stand_on_top'),
(750, 'arabian_snake', '0,0,0', 1, 1, 0, 'solid,custom_data_numeric_state'),
(751, 'arabian_swords', '0,0,0', 1, 1, 0, 'wall_item'),
(752, 'arabian_teamk', '0,0,0', 1, 1, 0, 'solid,custom_data_true_false'),
(753, 'arabian_tetbl', '0,0,0', 1, 1, 1, 'solid,can_stack_on_top'),
(754, 'arabian_tray1', '0,0,0', 1, 1, 0, 'solid'),
(755, 'arabian_tray2', '0,0,0', 1, 1, 0, 'solid'),
(756, 'arabian_tray3', '0,0,0', 1, 1, 0, 'solid'),
(757, 'arabian_tray4', '0,0,0', 1, 1, 0, 'solid'),
(758, 'arabian_wndw', '0,0,0', 1, 1, 0, 'wall_item'),
(759, 'arabian_wall', '0,0,0', 1, 1, 0.1, 'wall_item'),
(760, 'arabian_tile', '0,0,0', 2, 2, 0.1, 'can_stand_on_top,can_stack_on_top');

INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `definition_id`, `item_specialspriteid`, `package_name`, `package_description`, `is_package`) VALUES
(627, 'arabian_bigtb', 46, 0, 5, 745, 0, NULL, NULL, 0),
(628, 'arabian_chair', 46, 0, 2, 746, 0, NULL, NULL, 0),
(629, 'arabian_divdr', 46, 0, 5, 747, 0, NULL, NULL, 0),
(630, 'arabian_pllw', 46, 0, 2, 748, 0, NULL, NULL, 0),
(631, 'arabian_rug', 46, 0, 3, 749, 0, NULL, NULL, 0),
(632, 'arabian_snake', 46, 0, 3, 750, 0, NULL, NULL, 0),
(633, 'arabian_swords', 46, 0, 4, 751, 0, NULL, NULL, 0),
(634, 'arabian_teamk', 46, 0, 6, 752, 0, NULL, NULL, 0),
(635, 'arabian_tetbl', 46, 0, 3, 753, 0, NULL, NULL, 0),
(636, 'arabian_tray1', 46, 0, 3, 754, 0, NULL, NULL, 0),
(637, 'arabian_tray2', 46, 0, 3, 755, 0, NULL, NULL, 0),
(638, 'arabian_tray3', 46, 0, 3, 756, 0, NULL, NULL, 0),
(639, 'arabian_tray4', 46, 0, 3, 757, 0, NULL, NULL, 0),
(640, 'arabian_wndw', 46, 0, 4, 758, 0, NULL, NULL, 0),
(641, 'arabian_wall', 46, 0, 3, 759, 0, NULL, NULL, 0),
(642, 'arabian_tile', 46, 0, 3, 760, 0, NULL, NULL, 0);

-- migrate:down

