-- migrate:up
UPDATE users SET rank = 3 WHERE rank = 2;
UPDATE users SET rank = 4 WHERE rank = 3;
UPDATE users SET rank = 5 WHERE rank = 4;
UPDATE users SET rank = 6 WHERE rank = 5;
UPDATE users SET rank = 7 WHERE rank = 6;

-- migrate:down
UPDATE users SET rank = 6 WHERE rank = 7;
UPDATE users SET rank = 5 WHERE rank = 6;
UPDATE users SET rank = 4 WHERE rank = 5;
UPDATE users SET rank = 3 WHERE rank = 4;
UPDATE users SET rank = 2 WHERE rank = 3;
