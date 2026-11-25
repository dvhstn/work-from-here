ALTER TABLE spaces
    DROP CONSTRAINT spaces_type_check;

ALTER TABLE spaces
    ADD CONSTRAINT spaces_type_check CHECK (type IN ('CAFE', 'HOT_DESK', 'BAR', 'HOTEL', 'LIBRARY', 'COWORKING'));
