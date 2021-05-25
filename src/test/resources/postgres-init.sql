DROP TABLE IF EXISTS users;
DROP TYPE IF EXISTS user_roles;
CREATE TYPE user_roles AS ENUM ('CUSTOMER', 'EMPLOYEE', 'ADMIN');
CREATE TABLE users
(
    id   serial PRIMARY KEY,
    name VARCHAR(255) NULL,
    active BOOLEAN DEFAULT TRUE,
    role user_roles
);

DROP TABLE IF EXISTS no_increment;
CREATE TABLE no_increment
(
    message VARCHAR(255) PRIMARY KEY,
    role VARCHAR(20) NULL
);