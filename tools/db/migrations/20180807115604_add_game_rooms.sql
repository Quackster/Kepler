-- migrate:up
INSERT INTO `rooms_categories` (`id`, `order_id`, `parent_id`, `isnode`, `name`, `public_spaces`, `allow_trading`, `minrole_access`, `minrole_setflatcat`) VALUES ('13', '0', '3', '0', 'Games', '1', '0', '1', '6');

INSERT INTO `rooms_models` (`id`, `model_id`, `model_name`, `door_x`, `door_y`, `door_z`, `door_dir`, `heightmap`, `usertype`) VALUES
(84, 'entryhall', 'entryhall', 17, 18, 1, 0, 'xx11xxxx11xxxx11xxxx|x1111111111111111111|11111111111111111111|11111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|x1111111111111111111|xxxxxxxxxxxxxxxxx11x', 1),
(85, 'hallA', 'hallA', 1, 1, 1, 4, '11xxxxxxxxxxxxxxx|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111', 1),
(86, 'hallB', 'hallB', 1, 1, 1, 4, 'x11xxxxxxxxxxxxxxxx|1111111111111111111|1111111111111111111|1111111111111111111|1111111111111111111|1111111111111111111|1111111111111111111|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx|111111xxxxxxxxxxxxx', 1),
(87, 'hallC', 'hallC', 1, 1, 1, 4, '11xxxxxxxxxxxxxxx|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111', 1),
(88, 'hallD', 'hallD', 1, 1, 1, 4, '11xxxxxxxxxxxxxxx|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111|11111111111111111', 1);

INSERT INTO `rooms` (`id`, `owner_id`, `category`, `name`, `description`, `model`, `ccts`, `wallpaper`, `floor`, `showname`, `superusers`, `accesstype`, `password`, `visitors_now`, `visitors_max`) VALUES
(61, '0', 13, 'Cunning Fox Gamehall', 'cunning_fox_gamehall', 'entryhall', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25),
(62, '0', 13, 'TicTacToe hall', 'cunning_fox_gamehall/1', 'hallA', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 1, 25),
(63, '0', 13, 'Battleships hall', 'cunning_fox_gamehall/2', 'hallB', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25),
(64, '0', 13, 'Chess hall', 'cunning_fox_gamehall/3', 'hallC', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25),
(65, '0', 13, 'Poker hall', 'cunning_fox_gamehall/4', 'hallD', 'hh_room_gamehall,hh_games', 0, 0, 1, 0, 0, '', 0, 25),
(66, '0', 13, 'Battleball Lobby', 'bb_lobby_beginner_0', 'bb_lobby_1', 'hh_game_bb,hh_game_bb_room,hh_game_bb_ui,hh_gamesys', 0, 0, 1, 0, 0, '', 0, 25),
(67, '0', 13, 'Snowstorm Lobby', 'sw_lobby_beginner_0', 'snowwar_lobby_1', 'hh_gamesys,hh_game_snowwar,hh_game_snowwar_room,hh_game_snowwar_ui', 0, 0, 1, 0, 0, '', 0, 25);

-- migrate:down

