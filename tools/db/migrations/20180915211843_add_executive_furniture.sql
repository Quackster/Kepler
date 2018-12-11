-- migrate:up
INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `index_visible`, `name_index`, `link_list`, `name`, `layout`, `image_headline`, `image_teasers`, `body`, `label_pick`, `label_extra_s`, `label_extra_t`) VALUES
(45, 27, 1, 1, 'Executive', '', 'Executive', 'ctlg_layout2', 'catalog_exe_headline1', 'catalog_exe_teaser', 'The Executive Furni is ideal for creating a sophisticated working environment, whether it be an office, a mafia headquarters or study!', 'Click on the item you want for more information', '', '');

INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `definition_id`, `item_specialspriteid`, `package_name`, `package_description`, `is_package`) VALUES
(608, 'exe_rug', 45, 0, 1, 727, 0, NULL, NULL, 0),
(609, 'exe_s_table', 45, 0, 2, 728, 0, NULL, NULL, 0),
(610, 'exe_bardesk', 45, 0, 3, 729, 0, NULL, NULL, 0),
(611, 'exe_chair', 45, 0, 2, 730, 0, NULL, NULL, 0),
(612, 'exe_chair2', 45, 0, 3, 731, 0, NULL, NULL, 0),
(613, 'exe_corner', 45, 0, 2, 732, 0, NULL, NULL, 0),
(614, 'exe_drinks', 45, 0, 2, 733, 0, NULL, NULL, 0),
(615, 'exe_sofa', 45, 0, 5, 734, 0, NULL, NULL, 0),
(616, 'exe_table', 45, 0, 5, 735, 0, NULL, NULL, 0),
(617, 'exe_plant', 45, 0, 5, 736, 0, NULL, NULL, 0),
(618, 'exe_lught', 45, 0, 5, 737, 0, NULL, NULL, 0),
(619, 'exe_cubelight', 45, 0, 5, 739, 0, NULL, NULL, 0),
(620, 'exe_artlamp', 45, 0, 5, 740, 0, NULL, NULL, 0),
(621, 'exe_map', 45, 0, 5, 741, 0, NULL, NULL, 0),
(622, 'exe_wfall', 45, 0, 5, 742, 0, NULL, NULL, 0),
(623, 'exe_globe', 45, 0, 5, 743, 0, NULL, NULL, 0),
(624, 'exe_elevator', 45, 0, 7, 744, 0, NULL, NULL, 0);

INSERT INTO `items_definitions` (`id`, `sprite`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES
(727, 'exe_rug', '0,0,0', 3, 3, 0.1, 'can_stand_on_top,can_stack_on_top'),
(728, 'exe_s_table', '0,0,0', 2, 2, 1, 'solid,can_stack_on_top,custom_data_numeric_state'),
(729, 'exe_bardesk', '0,0,0', 1, 1, 1, 'solid,can_stack_on_top'),
(730, 'exe_chair', '0,0,0', 1, 1, 1, 'can_sit_on_top'),
(731, 'exe_chair2', '0,0,0', 1, 1, 1, 'can_sit_on_top'),
(732, 'exe_corner', '0,0,0', 1, 1, 1, 'solid,can_stack_on_top'),
(733, 'exe_drinks', '0,0,0', 1, 1, 0, 'solid'),
(734, 'exe_sofa', '0,0,0', 3, 1, 1, 'can_sit_on_top'),
(735, 'exe_table', '0,0,0', 3, 2, 0, 'solid,custom_data_numeric_state'),
(736, 'exe_plant', '0,0,0', 1, 1, 0, 'solid,custom_data_numeric_state'),
(737, 'exe_light', '0,0,0', 1, 1, 0, 'can_stand_on_top,can_stack_on_top,custom_data_numeric_state'),
(738, 'exe_gate', '0,0,0', 1, 1, 0, 'solid,requires_rights_for_interaction,door'),
(739, 'exe_cubelight', '0,0,0', 1, 1, 0, 'solid,custom_data_numeric_state'),
(740, 'exe_artlamp', '0,0,0', 1, 1, 0, 'solid,custom_data_numeric_state'),
(741, 'exe_map', '0,0,0', 1, 1, 0, 'wall_item'),
(742, 'exe_wfall', '0,0,0', 1, 1, 0, 'wall_item'),
(743, 'exe_globe', '0,0,0', 1, 1, 0, 'solid,custom_data_numeric_state'),
(744, 'exe_elevator', '0,0,0', 1, 1, 0, 'solid,requires_touching_for_interaction,custom_data_true_false,teleporter');

-- migrate:down

