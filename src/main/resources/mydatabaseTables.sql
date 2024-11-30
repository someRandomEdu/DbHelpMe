use mydatabase;

CREATE TABLE `accounts` (
   `is_admin` bit(1) DEFAULT NULL,
   `id` bigint NOT NULL AUTO_INCREMENT,
   `password` varchar(255) DEFAULT NULL,
   `username` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `books` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `title` varchar(255) NOT NULL,
   `author` varchar(255) NOT NULL,
   `publisher` varchar(255) NOT NULL DEFAULT (_utf8mb4''),
   `description` varchar(255) NOT NULL DEFAULT (_utf8mb4''),
   PRIMARY KEY (`id`),
   UNIQUE KEY `title` (`title`,`author`)
 ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `rent_data` (
   `rented` bit(1) DEFAULT NULL,
   `account_id` bigint DEFAULT NULL,
   `book_id` bigint DEFAULT NULL,
   `id` bigint NOT NULL AUTO_INCREMENT,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;