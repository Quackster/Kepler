DROP TABLE IF EXISTS `users_bans`;
CREATE TABLE `users_bans` (
  `ban_type` enum('MACHINE_ID','IP_ADDRESS','USER_ID') NOT NULL,
  `banned_value` varchar(250) NOT NULL,
  `message` text NOT NULL,
  `banned_until` bigint(11) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users_bans`
--
ALTER TABLE `users_bans`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users_bans`
--
ALTER TABLE `users_bans`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;