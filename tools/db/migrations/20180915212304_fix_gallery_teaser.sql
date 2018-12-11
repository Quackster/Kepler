-- migrate:up
UPDATE catalogue_pages SET image_teasers = 'catalog_posters_teaser1,' WHERE name = 'Gallery';

-- migrate:down

