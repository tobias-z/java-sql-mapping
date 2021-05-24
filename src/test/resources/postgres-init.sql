DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id   serial PRIMARY KEY,
    name VARCHAR(255) NULL,
    active BOOLEAN DEFAULT TRUE
);

DROP TABLE IF EXISTS no_increment;
CREATE TABLE no_increment
(
    message VARCHAR(255) PRIMARY KEY
);