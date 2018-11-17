-- migrate:up
UPDATE `public_items` SET rotation = 7 WHERE id = 'e705';
UPDATE `public_items` SET rotation = 1 WHERE id = 'z704';
UPDATE `public_items` SET rotation = 3 WHERE id = 'y934';
UPDATE `public_items` SET rotation = 5 WHERE id = 'f589';

UPDATE rooms_models SET trigger_class = 'flat_trigger' WHERE trigger_class = 'FlatTrigger';
UPDATE rooms_models SET trigger_class = 'battleball_lobby_trigger' WHERE trigger_class = 'BattleballLobbyTrigger';
UPDATE rooms_models SET trigger_class = 'snowstorm_lobby_trigger' WHERE trigger_class = 'SnowstormLobbyTrigger';
UPDATE rooms_models SET trigger_class = 'space_cafe_trigger' WHERE trigger_class = 'SpaceCafeTrigger';
UPDATE rooms_models SET trigger_class = 'habbo_lido_trigger' WHERE trigger_class = 'HabboLidoTrigger';
UPDATE rooms_models SET trigger_class = 'rooftop_rumble_trigger' WHERE trigger_class = 'RooftopRumbleTrigger';
UPDATE rooms_models SET trigger_class = 'diving_deck_trigger' WHERE trigger_class = 'DivingDeckTrigger';
UPDATE rooms_models SET trigger_class = 'none' WHERE trigger_class = '';

ALTER TABLE `rooms_models` CHANGE `trigger_class` `trigger_class` ENUM('flat_trigger','battleball_lobby_trigger','snowstorm_lobby_trigger','space_cafe_trigger','habbo_lido_trigger','rooftop_rumble_trigger','diving_deck_trigger','none') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('g243', 'md_a', 'poolEnter', '11', '11', '7', '2', '0.001', '1', '1', 'can_stand_on_top', ''), 
('n678', 'md_a', 'poolExit', '12', '11', '4', '6', '0.001', '1', '1', 'can_stand_on_top', ''),
('h317', 'md_a', 'poolExit', '12', '12', '4', '6', '0.001', '1', '1', 'can_stand_on_top', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('t743', 'hallD', 'streetlight', '0', '9', '1', '0', '0.001', '1', '1', 'solid', ''),
('j332', 'hallD', 'streetlight', '0', '15', '1', '0', '0.001', '1', '1', 'solid', ''),
('w621', 'hallD', 'streetlight', '8', '1', '1', '0', '0.001', '1', '1', 'solid', ''),
('o742', 'hallD', 'streetlight', '14', '1', '1', '0', '0.001', '1', '1', 'solid', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES ('t546', 'pub_a', 'bar_gate', '9', '9', '2', '0', '0.001', '1', '1', 'solid,invisible', '');

UPDATE public_items SET top_height = '1.5' WHERE sprite = 'pub_chair' AND room_model = 'pub_a';

UPDATE public_items SET behaviour = 'can_stand_on_top,invisible' WHERE sprite IN ('poolEnter','poolExit','poolBooth','poolLift');

UPDATE public_items SET behaviour = 'can_sit_on_top',top_height = '1.0' WHERE sprite = 'table2' AND room_model = 'beauty_salon0';

UPDATE public_items SET behaviour = 'can_sit_on_top',top_height = '1.0' WHERE sprite IN ('elephantcouch1','elephantcouch2','elephantcouch3','elephantcouch4') AND room_model = 'club_mammoth';

UPDATE public_items SET behaviour = 'can_sit_on_top',top_height = '1.0' WHERE sprite IN ('gate_table','gate_table1','gate_table2','gate_table3');


INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('j635', 'bar_b', 'djtable', 16, 22, 4, 0, 0.001, 1, 2, 'solid,invisible', '');



INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('b523', 'library', 'invisible_table', 28, 28, 1, 0, 0.001, 2, 2, 'solid,invisible', '');


INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('j725', 'tearoom', 'invisible_table', 2, 7, 3, 0, 0.002, 2, 2, 'solid,invisible', '');
INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('d646', 'tearoom', 'invisible_table', 2, 13, 3, 0, 0.002, 2, 2, 'solid,invisible', '');
INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('r452', 'tearoom', 'invisible_table', 16, 10, 3, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('y233', 'tearoom', 'invisible_table', 19, 3, 3, 0, 0.002, 1, 1, 'solid,invisible', '');
INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('c323', 'tearoom', 'invisible_table', 14, 3, 3, 0, 0.002, 1, 1, 'solid,invisible', '');


INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('f522', 'cr_staff', 'invisible_table', 6, 8, 1, 0, 0.001, 3, 6, 'solid,invisible', '');

UPDATE public_items SET length = 2, width = 3 WHERE id = 'd375' AND room_model = 'cr_staff';
UPDATE public_items SET length = 2, width = 2 WHERE id = 'r333' AND room_model = 'cr_staff';

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('b545', 'sport', 'invisible_barrier', 11, 14, 1, 0, 0.002, 1, 4, 'solid,invisible', '');


INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('y423', 'cafe_ole', 'invisible_table', 4, 17, 1, 0, 0.002, 2, 3, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('v682', 'cafe_ole', 'invisible_table', 12, 16, 1, 0, 0.002, 2, 3, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('l032', 'cafe_ole', 'invisible_table', 15, 1, 1, 0, 0.002, 2, 3, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('j246', 'cafe_ole', 'invisible_table', 9, 5, 1, 0, 0.002, 2, 2, 'solid,invisible', '');


INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('r563', 'malja_bar_a', 'invisible_table', 6, 3, 4, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('m743', 'malja_bar_a', 'invisible_table', 7, 14, 1, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('w662', 'malja_bar_a', 'invisible_table', 13, 15, 1, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('b742', 'malja_bar_a', 'invisible_table', 1, 16, 1, 0, 0.002, 2, 2, 'solid,invisible', '');


INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('d334', 'malja_bar_b', 'invisible_table', 4, 1, 3, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('ml43', 'malja_bar_b', 'invisible_table', 2, 17, 3, 0, 0.002, 2, 2, 'solid,invisible', '');


INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('x343', 'pizza', 'invisible_table', 1, 17, 1, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('x562', 'pizza', 'invisible_table', 1, 9, 1, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('w434', 'pizza', 'invisible_table', 12, 21, 1, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('f422', 'pizza', 'invisible_table', 14, 4, 0, 0, 0.002, 2, 2, 'solid,invisible', '');

INSERT INTO `public_items` (`id`, `room_model`, `sprite`, `x`, `y`, `z`, `rotation`, `top_height`, `length`, `width`, `behaviour`, `current_program`) VALUES
('v342', 'pizza', 'invisible_table', 14, 11, 0, 0, 0.002, 2, 2, 'solid,invisible', '');

UPDATE `rooms_models` SET `heightmap` = 'xxxxxxxxxxxxxx44|xxxx444444444444|xxxx444444444444|xxxx444444444444|xxxx444444444444|xxxx444444444444|xxxxxxxxxxxxx333|1111111111111222|1111111111111111|1111111111111111|1111111111111111|1111111111111111|1111111111111111|1111111111111111|1111111111111111|1111111111111111|1111111111111111|1111111111111111|1111111111111111|111111111xxxxxxx|xxx11111xxxxxxxx|11111111xxxxxxxx|11111111xxxxxxxx|11111111xxxxxxxx|11111111xxxxxxxx' WHERE `model_id` = 'malja_bar_a';

-- migrate:down

