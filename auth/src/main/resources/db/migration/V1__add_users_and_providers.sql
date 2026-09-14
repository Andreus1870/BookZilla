CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    phone VARCHAR(32),
    country VARCHAR(100),
    city VARCHAR(100),
    registration_date TIMESTAMPTZ NOT NULL
);

CREATE TABLE providers (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
);