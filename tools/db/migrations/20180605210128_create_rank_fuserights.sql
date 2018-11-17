-- migrate:up
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
(2, 'fuse_extended_buddylist'),
(2, 'fuse_habbo_chooser'),
(2, 'fuse_furni_chooser'),
(2, 'fuse_room_queue_club'),
(2, 'fuse_priority_access'),
(2, 'fuse_use_special_room_layouts'),
(2, 'fuse_use_club_dance'),
(3, 'fuse_enter_full_rooms'),
(4, 'fuse_enter_locked_rooms'),
(4, 'fuse_kick'),
(4, 'fuse_mute'),
(5, 'fuse_ban'),
(5, 'fuse_room_mute'),
(5, 'fuse_room_kick'),
(5, 'fuse_receive_calls_for_help'),
(5, 'fuse_remove_stickies'),
(6, 'fuse_mod'),
(6, 'fuse_superban'),
(6, 'fuse_pick_up_any_furni'),
(6, 'fuse_ignore_room_owner'),
(6, 'fuse_any_room_controller'),
(3, 'fuse_room_alert'),
(6, 'fuse_moderator_access'),
(7, 'fuse_administrator_access'),
(7, 'fuse_see_flat_ids'),
(6, 'fuse_credits');

-- migrate:down
DROP TABLE `rank_fuserights`;
