ALTER TABLE `users` ADD `favourite_group` INT(11) NOT NULL DEFAULT '0' AFTER `snowstorm_points`;

CREATE TABLE `groups_details` (
  `id` int(10) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` mediumtext NOT NULL,
  `owner_id` int(10) NOT NULL,
  `badge` mediumtext NOT NULL DEFAULT 'b0503Xs09114s05013s05015',
  `group_type` tinyint(3) UNSIGNED NOT NULL DEFAULT 0,
  `forum_type` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `forum_premission` tinyint(1) UNSIGNED NOT NULL DEFAULT 0,
  `alias` varchar(30) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `groups_memberships`
--

CREATE TABLE `groups_memberships` (
  `user_id` int(10) NOT NULL,
  `group_id` int(10) NOT NULL,
  `member_rank` enum('3','2','1') NOT NULL DEFAULT '1',
  `is_pending` tinyint(11) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for table `groups_details`
--
ALTER TABLE `groups_details`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `alias` (`alias`);

--
-- Indexes for table `groups_memberships`
--
ALTER TABLE `groups_memberships`
  ADD KEY `userid` (`user_id`),
  ADD KEY `groupid` (`group_id`),
  ADD KEY `group_id` (`group_id`),
  ADD KEY `user_id` (`user_id`);

