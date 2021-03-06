DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NULL,
    active BIT DEFAULT 1,
    role varchar(10) NOT NULL CHECK (role IN('CUSTOMER', 'EMPLOYEE', 'ADMIN')) DEFAULT 'CUSTOMER'
);

DROP TABLE IF EXISTS no_increment;
CREATE TABLE no_increment
(
    message VARCHAR(255) PRIMARY KEY,
    role VARCHAR(20) NULL
);
