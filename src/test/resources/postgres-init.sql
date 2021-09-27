DROP TABLE IF EXISTS users;
DROP TYPE IF EXISTS user_roles;
CREATE TABLE users
(
    id   serial PRIMARY KEY,
    name VARCHAR(255) NULL,
    active BOOLEAN DEFAULT TRUE,
    role VARCHAR(20)
);

DROP TABLE IF EXISTS no_increment;
CREATE TABLE no_increment
(
    message VARCHAR(255) PRIMARY KEY,
    role VARCHAR(20) NULL
);