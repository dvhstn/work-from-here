ALTER TABLE spaces
    ADD CONSTRAINT spaces_type_check CHECK (type IN ('CAFE', 'HOT_DESK'));

CREATE UNIQUE INDEX spaces_name_lower_unique ON spaces (lower(name));
