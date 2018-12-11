-- migrate:up
ALTER TABLE users ADD tutorial_finished tinyint(1) DEFAULT 0 NOT NULL;

-- migrate:down

