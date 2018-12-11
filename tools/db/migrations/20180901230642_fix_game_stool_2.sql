-- migrate:up
UPDATE public_items SET id = 'c643' WHERE x = 3 AND y = 9 AND room_model = 'bb_lobby_1';

-- migrate:down

