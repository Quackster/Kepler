-- migrate:up
CREATE TABLE `housekeeping_audit_log` (
  `action` enum('alert_user','kick_user','ban_user','room_alert','room_kick') NOT NULL,
  `user_id` int(11) NOT NULL,
  `target_id` int(11) NOT NULL DEFAULT -1,
  `message`  varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `extra_notes`  varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

-- migrate:down

