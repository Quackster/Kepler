-- migrate:up
ALTER TABLE users ADD allow_friend_requests tinyint(1) DEFAULT 1 NOT NULL AFTER allow_stalking;

-- migrate:down
ALTER TABLE users DROP COLUMN allow_friend_requests;
