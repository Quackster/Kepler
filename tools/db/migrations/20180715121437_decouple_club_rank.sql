-- migrate:up
UPDATE users SET credits = credits + 25 WHERE rank = 2;
UPDATE users SET rank = 1 WHERE rank = 2;
UPDATE users SET rank = 2 WHERE rank = 3;
UPDATE users SET rank = 3 WHERE rank = 4;
UPDATE users SET rank = 4 WHERE rank = 5;
UPDATE users SET rank = 5 WHERE rank = 6;
UPDATE users SET rank = 6 WHERE rank = 7;

DROP TABLE `rank_fuserights`;
CREATE TABLE `rank_fuserights` (
  `min_rank` int(11) NOT NULL,
  `fuseright` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `rank_fuserights` (`min_rank`, `fuseright`) VALUES
(1, 'default'),
(1, 'fuse_login'),
(1, 'fuse_buy_credits'),
(1, 'fuse_trade'),
(1, 'fuse_room_queue_default'),
(2, 'fuse_enter_full_rooms'),
(3, 'fuse_enter_locked_rooms'),
(3, 'fuse_kick'),
(3, 'fuse_mute'),
(4, 'fuse_ban'),
(4, 'fuse_room_mute'),
(4, 'fuse_room_kick'),
(4, 'fuse_receive_calls_for_help'),
(4, 'fuse_remove_stickies'),
(5, 'fuse_mod'),
(5, 'fuse_superban'),
(5, 'fuse_pick_up_any_furni'),
(5, 'fuse_ignore_room_owner'),
(5, 'fuse_any_room_controller'),
(2, 'fuse_room_alert'),
(5, 'fuse_moderator_access'),
(6, 'fuse_administrator_access'),
(6, 'fuse_see_flat_ids'),
(5, 'fuse_credits');

-- migrate:down

