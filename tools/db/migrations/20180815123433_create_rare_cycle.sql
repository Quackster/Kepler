-- migrate:up
CREATE TABLE `rare_cycle` (
  `sale_code` varchar(255) NOT NULL,
  `reuse_time` bigint(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `rare_cycle`
  ADD PRIMARY KEY (`sale_code`);

-- migrate:down

