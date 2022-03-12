ALTER TABLE `users`
ADD `status` varchar(50) NOT NULL DEFAULT 'offline' AFTER `snowstorm_points`,
ADD  `group_id` int(11) NOT NULL DEFAULT 0  AFTER `status`;


CREATE TABLE `tags` (
  `id` int(11) NOT NULL,
  `tag` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `group_id` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `tags`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `tags`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;


CREATE TABLE `credit_log` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `type` varchar(255) NOT NULL DEFAULT 'stuff_store',
  `credits` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


ALTER TABLE `credit_log`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `credit_log`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;
