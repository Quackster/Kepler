CREATE TABLE `rooms_ads` (
  `id` int(11) NOT NULL,
  `is_loading_ad` tinyint(1) NOT NULL DEFAULT 0,
  `room_id` int(11) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `image` mediumtext NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



--
-- Indexes for table `rooms_ads`
--
ALTER TABLE `rooms_ads`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `rooms_ads`
--
ALTER TABLE `rooms_ads`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=84;
COMMIT;