ALTER TABLE spaces
    ADD COLUMN image_key        VARCHAR(255),
    ADD COLUMN image_alt        VARCHAR(255),
    ADD COLUMN image_credit     VARCHAR(255),
    ADD COLUMN image_license    VARCHAR(100),
    ADD COLUMN image_source_url VARCHAR(500);
