-- migrate:up
INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `index_visible`, `name_index`, `link_list`, `name`, `layout`, `image_headline`, `image_teasers`, `body`, `label_pick`, `label_extra_s`, `label_extra_t`) VALUES
(43, 43, 5, 1, 'Inflatable Chairs', '', 'Inflatable Chairs', 'ctlg_layout2', 'catalog_rares_headline1', '', 'Yet another rares page.', 'Click on the item you want for more information', NULL, NULL);

INSERT INTO `catalogue_pages` (`id`, `order_id`, `min_role`, `index_visible`, `name_index`, `link_list`, `name`, `layout`, `image_headline`, `image_teasers`, `body`, `label_pick`, `label_extra_s`, `label_extra_t`) VALUES
(44, 44, 5, 1, 'Rares Mixed', '', 'Rares Mixed', 'ctlg_layout2', 'catalog_rares_headline1', '', 'Yet another rares page.', 'Click on the item you want for more information', NULL, NULL);

INSERT INTO `items_definitions` (`id`, `sprite`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES
(708, 'rubberchair*1', '#4193D6,#FFFFFF,#FFFFFF', 1, 1, 1, 'can_sit_on_top'),
(709, 'rubberchair*2', '#FF8B8B,#FFFFFF,#FFFFFF', 1, 1, 1, 'can_sit_on_top'),
(710, 'rubberchair*3', '#FF8000,#FFFFFF,#FFFFFF', 1, 1, 1, 'can_sit_on_top'),
(711, 'rubberchair*4', '#00E5E2,#FFFFFF,#FFFFFF', 1, 1, 1, 'can_sit_on_top'),
(712, 'rubberchair*5', '#A1DC67,#FFFFFF,#FFFFFF', 1, 1, 1, 'can_sit_on_top'),
(713, 'rubberchair*6', '#B357FF,#FFFFFF,#FFFFFF', 1, 1, 1, 'can_sit_on_top'),
(714, 'rubberchair*7', '#CFCFCF,#FFFFFF,#FFFFFF', 1, 1, 1, 'can_sit_on_top'),
(715, 'rubberchair*8', '#333333,#FFFFFF,#FFFFFF', 1, 1, 1, 'can_sit_on_top');

INSERT INTO `items_definitions` (`id`, `sprite`, `colour`, `length`, `width`, `top_height`, `behaviour`) VALUES
(716, 'spyro', '0,0,0', 1, 1, 0, 'solid'),
(717, 'rare_daffodil_rug', '0,0,0', 2, 2, 0, 'can_stand_on_top,can_stack_on_top'),
(718, 'md_limukaappi', '0,0,0', 1, 1, 0, 'solid'),
(719, 'samovar', '0,0,0', 1, 1, 0, 'solid'),
(720, 'redhologram', '0,0,0', 1, 1, 0, 'solid,custom_data_on_off'),
(721, 'typingmachine', '0,0,0', 1, 1, 0, 'solid'),
(722, 'hologram', '0,0,0', 1, 1, 0, 'solid,custom_data_on_off'),
(723, 'prize1', '0,0,0', 1, 1, 0, 'solid'),
(724, 'prize2', '0,0,0', 1, 1, 0, 'solid'),
(725, 'prize3', '0,0,0', 1, 1, 0, 'solid'),
(726, 'rare_snowrug', '0,0,0', 2, 2, 0, 'can_stand_on_top,can_stack_on_top');

INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `definition_id`, `item_specialspriteid`, `package_name`, `package_description`, `is_package`) VALUES
(587, 'rubber_chair*1', 43, 0, 25, 708, 0, NULL, NULL, 0),
(588, 'rubber_chair*2', 43, 0, 25, 709, 0, NULL, NULL, 0),
(589, 'rubber_chair*3', 43, 0, 25, 710, 0, NULL, NULL, 0),
(590, 'rubber_chair*4', 43, 0, 25, 711, 0, NULL, NULL, 0),
(591, 'rubber_chair*5', 43, 0, 25, 712, 0, NULL, NULL, 0),
(592, 'rubber_chair*6', 43, 0, 25, 713, 0, NULL, NULL, 0),
(593, 'rubber_chair*7', 43, 0, 25, 714, 0, NULL, NULL, 0),
(594, 'rubber_chair*8', 43, 0, 25, 715, 0, NULL, NULL, 0);

INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `definition_id`, `item_specialspriteid`, `package_name`, `package_description`, `is_package`) VALUES
(595, 'spyro', 44, 0, 25, 716, 0, NULL, NULL, 0),
(596, 'throne', 44, 0, 50, 107, 0, NULL, NULL, 0),
(597, 'rare_daffodil_rug', 44, 0, 25, 717, 0, NULL, NULL, 0),
(598, 'md_limukaappi', 44, 0, 25, 718, 0, NULL, NULL, 0),
(599, 'samovar', 44, 0, 30, 719, 0, NULL, NULL, 0),
(600, 'redhologram', 44, 0, 20, 720, 0, NULL, NULL, 0),
(601, 'typingmachine', 44, 0, 20, 721, 0, NULL, NULL, 0),
(602, 'hologram', 44, 0, 20, 722, 0, NULL, NULL, 0),
(603, 'prize1', 44, 0, 15, 723, 0, NULL, NULL, 0),
(604, 'prize2', 44, 0, 15, 724, 0, NULL, NULL, 0),
(605, 'prize3', 44, 0, 15, 725, 0, NULL, NULL, 0),
(606, 'rare_snow_rug', 44, 0, 25, 726, 0, NULL, NULL, 0);


INSERT INTO `catalogue_items` (`id`, `sale_code`, `page_id`, `order_id`, `price`, `definition_id`, `item_specialspriteid`, `package_name`, `package_description`, `is_package`) VALUES (NULL, 'rare_poster_pedobear', '44', '1', '25', '251', '1338', NULL, NULL, '0');

-- migrate:down

