-- migrate:up
UPDATE `catalogue_items` SET order_id = 9 WHERE sale_code = 'soundset6';
UPDATE `catalogue_items` SET order_id = 9 WHERE sale_code = 'soundset7';

INSERT INTO `items_definitions` VALUES (358,'sound_set_2','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
UPDATE `catalogue_items` SET package_name = '', package_description = '', order_id = 9, definition_id = 358 WHERE sale_code = 'soundset2';

INSERT INTO `items_definitions` VALUES (359,'sound_set_4','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
UPDATE `catalogue_items` SET order_id = 9, definition_id = 359 WHERE sale_code = 'soundset4';

INSERT INTO `items_definitions` VALUES (360,'sound_set_5','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
UPDATE `catalogue_items` SET order_id = 9, definition_id = 360 WHERE sale_code = 'soundset5';

INSERT INTO `items_definitions` VALUES (356,'sound_set_9','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
UPDATE `catalogue_items` SET order_id = 9, definition_id = 356 WHERE sale_code = 'soundset9';

INSERT INTO `items_definitions` VALUES (357,'sound_set_10','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (458,'soundset10',11,9,3,357,0,'','',0);

INSERT INTO `items_definitions` VALUES (361,'sound_set_11','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (459,'soundset11',11,9,3,361,0,'','',0);

INSERT INTO `items_definitions` VALUES (362,'sound_set_12','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (460,'soundset12',11,9,3,362,0,'','',0);

INSERT INTO `items_definitions` VALUES (363,'sound_set_13','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (461,'soundset13',11,9,3,363,0,'','',0);

INSERT INTO `items_definitions` VALUES (364,'sound_set_14','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (462,'soundset14',11,9,3,364,0,'','',0);

INSERT INTO `items_definitions` VALUES (365,'sound_set_15','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (463,'soundset15',11,9,3,365,0,'','',0);

INSERT INTO `items_definitions` VALUES (366,'sound_set_16','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (464,'soundset16',11,9,3,366,0,'','',0);

INSERT INTO `items_definitions` VALUES (367,'sound_set_17','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (465,'soundset17',11,9,3,367,0,'','',0);

INSERT INTO `items_definitions` VALUES (368,'sound_set_18','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (466,'soundset18',11,9,3,368,0,'','',0);

INSERT INTO `items_definitions` VALUES (369,'sound_set_19','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (467,'soundset19',11,9,3,369,0,'','',0);

INSERT INTO `items_definitions` VALUES (370,'sound_set_20','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (468,'soundset20',11,9,3,370,0,'','',0);

INSERT INTO `items_definitions` VALUES (371,'sound_set_21','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (469,'soundset21',11,9,3,371,0,'','',0);

INSERT INTO `items_definitions` VALUES (372,'sound_set_22','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (470,'soundset21',11,9,3,372,0,'','',0);

INSERT INTO `items_definitions` VALUES (373,'sound_set_23','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (471,'soundset23',11,9,3,373,0,'','',0);

INSERT INTO `items_definitions` VALUES (374,'sound_set_24','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (472,'soundset24',11,9,3,374,0,'','',0);

INSERT INTO `items_definitions` VALUES (375,'sound_set_25','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (473,'soundset25',11,9,3,375,0,'','',0);

INSERT INTO `items_definitions` VALUES (376,'sound_set_26','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (474,'soundset26',11,9,3,376,0,'','',0);

INSERT INTO `items_definitions` VALUES (377,'sound_set_27','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (475,'soundset27',11,9,3,377,0,'','',0);

INSERT INTO `items_definitions` VALUES (378,'sound_set_28','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (476,'soundset28',11,9,3,378,0,'','',0);

INSERT INTO `items_definitions` VALUES (379,'sound_set_29','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (477,'soundset29',11,9,3,379,0,'','',0);

INSERT INTO `items_definitions` VALUES (380,'sound_set_30','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (478,'soundset30',11,9,3,380,0,'','',0);

INSERT INTO `items_definitions` VALUES (381,'sound_set_31','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (479,'soundset31',11,9,3,381,0,'','',0);

INSERT INTO `items_definitions` VALUES (382,'sound_set_32','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (480,'soundset32',11,9,3,382,0,'','',0);

INSERT INTO `items_definitions` VALUES (383,'sound_set_33','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (481,'soundset33',11,9,3,383,0,'','',0);

INSERT INTO `items_definitions` VALUES (384,'sound_set_34','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (482,'soundset34',11,9,3,384,0,'','',0);

INSERT INTO `items_definitions` VALUES (385,'sound_set_35','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (483,'soundset35',11,9,3,385,0,'','',0);

INSERT INTO `items_definitions` VALUES (386,'sound_set_36','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (484,'soundset36',11,9,3,386,0,'','',0);

INSERT INTO `items_definitions` VALUES (387,'sound_set_37','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (485,'soundset37',11,9,3,387,0,'','',0);

INSERT INTO `items_definitions` VALUES (388,'sound_set_38','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (486,'soundset38',11,9,3,388,0,'','',0);

INSERT INTO `items_definitions` VALUES (389,'sound_set_39','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (487,'soundset39',11,9,3,389,0,'','',0);

INSERT INTO `items_definitions` VALUES (390,'sound_set_40','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (488,'soundset40',11,9,3,390,0,'','',0);

INSERT INTO `items_definitions` VALUES (391,'sound_set_41','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (489,'soundset41',11,9,3,391,0,'','',0);

INSERT INTO `items_definitions` VALUES (392,'sound_set_42','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (490,'soundset42',11,9,3,392,0,'','',0);

INSERT INTO `items_definitions` VALUES (393,'sound_set_43','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (491,'soundset43',11,9,3,393,0,'','',0);

INSERT INTO `items_definitions` VALUES (394,'sound_set_44','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (492,'soundset44',11,9,3,394,0,'','',0);

INSERT INTO `items_definitions` VALUES (395,'sound_set_45','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (493,'soundset45',11,9,3,395,0,'','',0);

INSERT INTO `items_definitions` VALUES (396,'sound_set_46','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (494,'soundset46',11,9,3,396,0,'','',0);

INSERT INTO `items_definitions` VALUES (397,'sound_set_47','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (495,'soundset47',11,9,3,397,0,'','',0);

INSERT INTO `items_definitions` VALUES (398,'sound_set_48','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (496,'soundset48',11,9,3,398,0,'','',0);

INSERT INTO `items_definitions` VALUES (399,'sound_set_49','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (497,'soundset49',11,9,3,399,0,'','',0);

INSERT INTO `items_definitions` VALUES (400,'sound_set_50','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (498,'soundset50',11,9,3,400,0,'','',0);

INSERT INTO `items_definitions` VALUES (401,'sound_set_51','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (499,'soundset51',11,9,3,401,0,'','',0);

INSERT INTO `items_definitions` VALUES (402,'sound_set_52','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (500,'soundset52',11,9,3,402,0,'','',0);

INSERT INTO `items_definitions` VALUES (403,'sound_set_53','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (501,'soundset53',11,9,3,403,0,'','',0);

INSERT INTO `items_definitions` VALUES (404,'sound_set_54','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (502,'soundset54',11,9,3,404,0,'','',0);

INSERT INTO `items_definitions` VALUES (405,'sound_set_55','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (503,'soundset55',11,9,3,405,0,'','',0);

INSERT INTO `items_definitions` VALUES (406,'sound_set_56','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (504,'soundset56',11,9,3,406,0,'','',0);

INSERT INTO `items_definitions` VALUES (407,'sound_set_57','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (505,'soundset57',11,9,3,407,0,'','',0);

INSERT INTO `items_definitions` VALUES (408,'sound_set_58','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (506,'soundset58',11,9,3,408,0,'','',0);

INSERT INTO `items_definitions` VALUES (409,'sound_set_59','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (507,'soundset59',11,9,3,409,0,'','',0);

INSERT INTO `items_definitions` VALUES (410,'sound_set_60','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (508,'soundset60',11,9,3,410,0,'','',0);

INSERT INTO `items_definitions` VALUES (411,'sound_set_61','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (509,'soundset61',11,9,3,411,0,'','',0);

INSERT INTO `items_definitions` VALUES (412,'sound_set_62','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (510,'soundset62',11,9,3,412,0,'','',0);

INSERT INTO `items_definitions` VALUES (413,'sound_set_63','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (511,'soundset63',11,9,3,413,0,'','',0);

INSERT INTO `items_definitions` VALUES (414,'sound_set_64','',1,1,0.1,'solid,can_stack_on_top,sound_machine_sample_set');
INSERT INTO `catalogue_items` VALUES (512,'soundset64',11,9,3,414,0,'','',0);

-- migrate:down

