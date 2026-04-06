CREATE TABLE IF NOT EXISTS spaces
(
    id             BIGSERIAL    NOT NULL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL UNIQUE,
    description    VARCHAR(255) NOT NULL,
    type           VARCHAR(50)  NOT NULL,
    wifi_available BOOLEAN      NOT NULL,
    wifi_password  VARCHAR(255)
);
