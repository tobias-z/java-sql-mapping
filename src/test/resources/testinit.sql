use chat_test;

DROP TABLE IF EXISTS users;
CREATE TABLE `chat_test`.`users`
(
    `id`   INT          NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS no_increment;
CREATE TABLE `chat_test`.`no_increment`
(
    `message` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`message`)
);
