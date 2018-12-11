-- migrate:up
DELETE FROM `rank_badges` WHERE badge = 'HC1' AND `rank` = 2;

-- migrate:down

