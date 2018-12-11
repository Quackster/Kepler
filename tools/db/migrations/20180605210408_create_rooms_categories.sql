-- migrate:up
CREATE TABLE `rooms_categories` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `parent_id` int(11) NOT NULL,
  `isnode` int(11) DEFAULT 0,
  `name` varchar(255) NOT NULL,
  `public_spaces` int(11) DEFAULT 0,
  `allow_trading` int(11) DEFAULT 0,
  `minrole_access` int(11) DEFAULT 1,
  `minrole_setflatcat` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `rooms_categories` (`id`, `order_id`, `parent_id`, `isnode`, `name`, `public_spaces`, `allow_trading`, `minrole_access`, `minrole_setflatcat`) VALUES
(2, 0, 0, 0, 'No category', 0, 0, 1, 1),
(3, 0, 0, 1, 'Public Rooms', 1, 0, 1, 6),
(4, 0, 0, 1, 'Guest Rooms', 0, 0, 1, 6),
(5, 0, 3, 0, 'Entertainment', 1, 0, 1, 6),
(6, 0, 3, 0, 'Restaurants and Cafes', 1, 0, 1, 6),
(7, 0, 3, 0, 'Lounges and Clubs', 1, 0, 1, 6),
(8, 0, 3, 0, 'Club-only Spaces', 1, 0, 1, 6),
(9, 0, 3, 0, 'Parks and Gardens', 1, 0, 1, 6),
(10, 0, 3, 0, 'Swimming Pools', 1, 0, 1, 6),
(11, 0, 3, 0, 'The Lobbies', 1, 0, 1, 6),
(12, -1, 3, 0, 'The Hallways', 1, 0, 1, 6),
(20, 0, 9, 0, 'The Laughing Lions Park', 1, 0, 1, 6),
(21, 0, 9, 0, 'The Green Heart', 1, 0, 1, 6),
(101, 0, 4, 0, 'Staff HQ', 0, 1, 4, 5),
(112, 0, 4, 0, 'Restaurant, Bar & Night Club Rooms', 0, 0, 1, 1),
(113, 0, 4, 0, 'Trade floor', 0, 1, 1, 1),
(114, 0, 4, 0, 'Chill, Chat & Discussion Rooms', 0, 0, 1, 1),
(115, 0, 4, 0, 'Hair Salons & Modelling Rooms', 0, 0, 1, 1),
(116, 0, 4, 0, 'Maze & Theme Park Rooms', 0, 0, 1, 1),
(117, 0, 4, 0, 'Gaming & Race Rooms', 0, 0, 1, 1),
(118, 0, 4, 0, 'Help Centre Rooms', 0, 0, 1, 1),
(120, 0, 4, 0, 'Miscellaneous', 0, 0, 1, 1);

-- migrate:down
DROP TABLE `rooms_categories`;
