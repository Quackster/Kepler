-- migrate:up
UPDATE rooms SET id = 1 WHERE id = 1001;
UPDATE rooms SET id = 2 WHERE id = 1003;
UPDATE rooms SET id = 3 WHERE id = 1004;
UPDATE rooms SET id = 4 WHERE id = 1005;
UPDATE rooms SET id = 5 WHERE id = 1006;
UPDATE rooms SET id = 6 WHERE id = 1007;
UPDATE rooms SET id = 7 WHERE id = 1008;
UPDATE rooms SET id = 8 WHERE id = 1009;
UPDATE rooms SET id = 9 WHERE id = 1010;
UPDATE rooms SET id = 10 WHERE id = 1011;
UPDATE rooms SET id = 11 WHERE id = 1012;
UPDATE rooms SET id = 12 WHERE id = 1013;
UPDATE rooms SET id = 13 WHERE id = 1014;
UPDATE rooms SET id = 14 WHERE id = 1015;
UPDATE rooms SET id = 15 WHERE id = 1016;
UPDATE rooms SET id = 16 WHERE id = 1017;
UPDATE rooms SET id = 17 WHERE id = 1018;
UPDATE rooms SET id = 18 WHERE id = 1019;
UPDATE rooms SET id = 19 WHERE id = 1020;
UPDATE rooms SET id = 20 WHERE id = 1022;
UPDATE rooms SET id = 21 WHERE id = 1023;
UPDATE rooms SET id = 22 WHERE id = 1024;
UPDATE rooms SET id = 23 WHERE id = 1025;
UPDATE rooms SET id = 24 WHERE id = 1026;
UPDATE rooms SET id = 25 WHERE id = 1027;
UPDATE rooms SET id = 26 WHERE id = 1028;
UPDATE rooms SET id = 27 WHERE id = 1029;
UPDATE rooms SET id = 28 WHERE id = 1030;
UPDATE rooms SET id = 29 WHERE id = 1031;
UPDATE rooms SET id = 30 WHERE id = 1032;
UPDATE rooms SET id = 31 WHERE id = 1033;
UPDATE rooms SET id = 32 WHERE id = 1034;
UPDATE rooms SET id = 33 WHERE id = 1035;
UPDATE rooms SET id = 34 WHERE id = 1036;
UPDATE rooms SET id = 35 WHERE id = 1037;
UPDATE rooms SET id = 36 WHERE id = 1038;
UPDATE rooms SET id = 37 WHERE id = 1039;
UPDATE rooms SET id = 38 WHERE id = 1040;
UPDATE rooms SET id = 39 WHERE id = 1041;
UPDATE rooms SET id = 40 WHERE id = 1042;
UPDATE rooms SET id = 41 WHERE id = 1043;
UPDATE rooms SET id = 42 WHERE id = 1044;
UPDATE rooms SET id = 43 WHERE id = 1045;
UPDATE rooms SET id = 44 WHERE id = 1046;
UPDATE rooms SET id = 45 WHERE id = 1047;
UPDATE rooms SET id = 46 WHERE id = 1048;
UPDATE rooms SET id = 47 WHERE id = 1049;
UPDATE rooms SET id = 48 WHERE id = 1050;
UPDATE rooms SET id = 49 WHERE id = 1051;
UPDATE rooms SET id = 50 WHERE id = 1052;
UPDATE rooms SET id = 51 WHERE id = 1053;
UPDATE rooms SET id = 52 WHERE id = 1054;
UPDATE rooms SET id = 53 WHERE id = 1055;

DELETE FROM `rooms_models` WHERE id = 71;

INSERT INTO `rooms_models` (`id`, `model_id`, `model_name`, `door_x`, `door_y`, `door_z`, `door_dir`, `heightmap`, `usertype`) VALUES
(80, 'hallway10', 'hallway10', 3, 23, 1, 1, 'xxxxxxxxxx00000000xxxx|xxxxxxxxxx00000000xxxx|xxxxxxxxxx00000000xxxx|xxxxxxxxxx00000000xxxx|xx1111xxxx0000xxxxxxxx|xx1111xxxx0000xxxxxxxx|xx1111xxxx0000xxxxxxxx|xx1111xxxx0000xxxxxxxx|11111111xx0000000000xx|11111111xx0000000000xx|11111111xx0000000000xx|11111111xx0000000000xx|11111111xxxxxxxx0000xx|11111111xxxxxxxx0000xx|11111111xxxxxxxx0000xx|11111111xxxxxxxx0000xx|1111111111111111000000|1111111111111111000000|1111111111111111000000|1111111111111111000000|1111111111111111000000|1111111111111111000000|1111111111111111000000|1111111111111111000000|xx1111xxxxxxxxxxxxxxxx|xx1111xxxxxxxxxxxxxxxx|xx1111xxxxxxxxxxxxxxxx|xx1111xxxxxxxxxxxxxxxx', 1),
(81, 'hallway11', 'hallway11', 20, 3, 0, 6, 'xxxx1111111100000000xxxx|xxxx1111111100000000xxxx|111111111111000000000000|111111111111000000000000|111111111111000000000000|111111111111000000000000|xxxx1111111100000000xxxx|xxxx1111111100000000xxxx|xxxxxxxxxxxx000000000000|xxxxxxxxxxxx000000000000|xxxxxxxxxx00000000000000|xxxxxxxxxx00000000000000|xxxxxxxxxx00000000000000|xxxxxxxxxx00000000000000|xxxxxxxxxxxx000000000000|xxxxxxxxxxxx000000000000|xxxxxxxxxxxx000000000000|xxxxxxxxxxxx000000000000|xxxxxxxx000000000000xxxx|xxxxxxxx000000000000xxxx|xxxxxxxx000000000000xxxx|xxxxxxxx000000000000xxxx', 1),
(79, 'hallway6', 'hallway6', 1, 10, 1, 2, 'xxxx1111111111111111xxxx|xxxx1111111111111111xxxx|xxxx1111111111111111xxxx|xxxx1111111111111111xxxx|xxxx1111xxxxxxxxxxxxxxxx|xxxx1111xxxxxxxxxxxxxxxx|xxxx1111xxxxxxxxxxxxxxxx|xxxx1111xxxxxxxxxxxxxxxx|xxxx1111111100000000xxxx|xxxx1111111100000000xxxx|111111111111000000000000|111111111111000000000000|111111111111000000000000|111111111111000000000000|xxxx1111111100000000xxxx|xxxx1111111100000000xxxx|xxxxxxxx1111xxxxxxxxxxxx|xxxxxxxx1111xxxxxxxxxxxx|xxxxxxxx1111xxxxxxxxxxxx|xxxxxxxx1111xxxxxxxxxxxx|xxxxxxxx111111111111xxxx|xxxxxxxx111111111111xxxx|xxxxxxxx111111111111xxxx|xxxxxxxx111111111111xxxx', 1),
(78, 'hallway7', 'hallway7', 7, 2, 1, 4, 'xxxxxx11xxxxxxxxxxxx|xxxxxx111xxxxxxxxxxx|xxxxxx1111xxxxxxxxxx|xxxxxx1111xxxxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx111111111111xxxx|xxxx111111111111xxxx|xxxx111111111111xxxx|xxxx111111111111xxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx11111111xxxxxxxx|xxxx000000000000xxxx|xxxx000000000000xxxx|00000000000000000000|00000000000000000000|00000000000000000000|00000000000000000000|xxxx000000000000xxxx|xxxx000000000000xxxx', 1),
(77, 'hallway8', 'hallway8', 15, 3, 0, 4, 'xxxxxxxxxxxxxx00xxxx0000|xxxxxxxxxxxxxx000xxx0000|xxxxxxxxxxxxxx0000xx0000|xxxxxxxxxxxxxx0000xx0000|xxxx11111111000000000000|xxxx11111111000000000000|xxxx11111111000000000000|xxxx11111111000000000000|xxxx11111111000000000000|xxxx11111111000000000000|xxxx11111111000000000000|xxxx11111111000000000000|xxxx11111111xxxx00000000|xxxx11111111xxxx00000000|111111111111xxxx00000000|111111111111xxxx00000000|111111111111xxxx00000000|111111111111xxxx00000000|xxxx11111111xxxx00000000|xxxx11111111xxxx00000000|xxxxxxxxxxxxxxxxxx0000xx|xxxxxxxxxxxxxxxxxx0000xx|xxxxxxxxxxxxxxxxxx0000xx|xxxxxxxxxxxxxxxxxx0000xx', 1),
(71, 'hallway9', 'hallway9', 21, 23, 0, 7, 'xxxxxxxxxxxxxx0000xxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000xxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000xxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000xxxxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxx000000000000000000000000xxxx|xxxx000000000000000000000000xxxx|00000000000000000000000000000000|00000000000000000000000000000000|00000000000000000000000000000000|00000000000000000000000000000000|xxxx000000000000000000000000xxxx|xxxx000000000000000000000000xxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx000000000000xxxxxxxx|xxxxxxxxxxxx000000000000xxxxxxxx|xxxxxxxxxxxx000000000000xxxxxxxx|xxxxxxxxxxxx000000000000xxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxx00000000xxxxxxxxxxxx|xxxxxxxxxxxxxx0000xxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000xxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000xxxxxxxxxxxxxx|xxxxxxxxxxxxxx0000xxxxxxxxxxxxxx', 1);

INSERT INTO `rooms` (`id`, `owner_id`, `category`, `name`, `description`, `model`, `ccts`, `wallpaper`, `floor`, `showname`, `superusers`, `accesstype`, `password`, `visitors_now`, `visitors_max`) VALUES
(54, '0', 12, 'Upper Hallways I', 'hallway_ii', 'hallway8', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25),
(55, '0', 12, 'Upper Hallways II', 'hallway_ii', 'hallway7', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25),
(56, '0', 12, 'Upper Hallways III', 'hallway_ii', 'hallway6', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25),
(57, '0', 12, 'Upper Hallways IV', 'hallway_ii', 'hallway10', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25),
(58, '0', 12, 'Upper Hallways V', 'hallway_ii', 'hallway11', 'hh_room_hallway', 0, 0, 1, 0, 0, '', 0, 25);

UPDATE `rooms_models` SET `door_x` = 2,`door_y` = 2,`door_z` = 0,`door_dir` = 2 WHERE model_id = 'hallway0';
UPDATE `rooms_models` SET `door_x` = 2,`door_y` = 14,`door_z` = 0,`door_dir` = 2 WHERE model_id = 'hallway1';
UPDATE `rooms_models` SET `door_x` = 14,`door_y` = 21,`door_z` = 1,`door_dir` = 0 WHERE model_id = 'hallway3';
UPDATE `rooms_models` SET `door_x` = 14,`door_y` = 2,`door_z` = 1,`door_dir` = 4 WHERE model_id = 'hallway5';
UPDATE `rooms_models` SET `door_x` = 29,`door_y` = 3,`door_z` = 1,`door_dir` = 6 WHERE model_id = 'hallway4';

UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 13;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 14;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 18;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 19;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 20;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 21;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 22;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 23;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 24;
UPDATE `rooms` SET `category` = 7 WHERE `rooms`.`id` = 26;
UPDATE `rooms` SET `category` = 12 WHERE `rooms`.`id` = 53;

UPDATE rooms SET name = "Habbo Lido B" WHERE model = "pool_b" AND owner_id = 0;

-- migrate:down

