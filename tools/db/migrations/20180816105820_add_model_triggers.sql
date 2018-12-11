-- migrate:up
ALTER TABLE `rooms_models` DROP `usertype`;
ALTER TABLE `rooms_models` ADD `trigger_class` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `heightmap`;

UPDATE `rooms_models` SET `trigger_class` = 'HabboLidoTrigger' WHERE `model_id` = 'pool_a';
UPDATE `rooms_models` SET `trigger_class` = 'DivingDeckTrigger' WHERE `model_id` = 'pool_b';
UPDATE `rooms_models` SET `trigger_class` = 'SpaceCafeTrigger' WHERE `model_id` = 'space_cafe';
UPDATE `rooms_models` SET `trigger_class` = 'RooftopRumbleTrigger' WHERE `model_id` = 'md_a';
UPDATE `rooms_models` SET `trigger_class` = 'BattleballLobbyTrigger' WHERE `model_id` = 'bb_lobby_1';
UPDATE `rooms_models` SET `trigger_class` = 'SnowStormLobbyTrigger' WHERE `model_id` = 'snowwar_lobby_1';

UPDATE rooms_models SET trigger_class = 'FlatTrigger' WHERE model_id LIKE 'model_%'

-- migrate:down

