-- migrate:up
INSERT INTO `rank_badges` (`rank`, badge) VALUES(5, 'ADM');
INSERT INTO `rank_badges` (`rank`, badge) VALUES(2, 'HC1');


-- migrate:down
DELETE FROM `rank_badges` WHERE badge = 'ADM' AND `rank` = 5;
DELETE FROM `rank_badges` WHERE badge = 'HC1' AND `rank` = 2;
