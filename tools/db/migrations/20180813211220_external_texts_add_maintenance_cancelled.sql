-- migrate:up
INSERT INTO external_texts (entry, text) VALUES ('maintenance_cancelled', 'Maintenance has been cancelled. The hotel will not shutdown.');

-- migrate:down
DELETE FROM external_texts WHERE entry = 'maintenance_cancelled' LIMIT 1;
