-- migrate:up
ALTER TABLE users ADD sound_enabled tinyint(1) DEFAULT 1 NOT NULL;

-- migrate:down
ALTER TABLE users DROP COLUMN sound_enabled;
