-- migrate:up
UPDATE `items_definitions` SET `behaviour` = 'solid,custom_data_on_off' WHERE `sprite` = 'tv_luxus';

-- migrate:down

