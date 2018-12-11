-- migrate:up
ALTER TABLE users MODIFY COLUMN sso_ticket varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' NOT NULL;

-- migrate:down
ALTER TABLE users MODIFY COLUMN sso_ticket varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
