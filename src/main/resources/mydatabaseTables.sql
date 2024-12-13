DROP DATABASE IF EXISTS mydatabase;
CREATE DATABASE mydatabase;
USE mydatabase;

CREATE TABLE accounts (
   id INT  AUTO_INCREMENT,
   `userFullName` varchar(255) not null,
   username VARCHAR(255) NOT NULL unique,
   password VARCHAR(255) Not NULL,
    is_admin BIT(1) DEFAULT NULL,
   email VARCHAR(255) not null unique,
   phone_number VARCHAR(20) not null,
   date_of_birth DATE not null,
   PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE books (
                       id INT NOT NULL AUTO_INCREMENT,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       publisher VARCHAR(255) NOT NULL DEFAULT '',
                       description VARCHAR(255) NOT NULL DEFAULT '',
                       category_id INT,
                       current INT NOT NULL DEFAULT 0,
                       PRIMARY KEY (id),
                       UNIQUE KEY title_author (title, author)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE rent_data (
   rented BIT(1) DEFAULT NULL,
   account_id INT DEFAULT NULL,
   book_id INT DEFAULT NULL,
   id INT NOT NULL AUTO_INCREMENT,
   status VARCHAR(50),
   borrow_from DATE,
   borrow_to DATE,
       PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE return_data (
                             account_id INT DEFAULT NULL,
                             book_id INT DEFAULT NULL,
                             id INT NOT NULL AUTO_INCREMENT,
                             borrow_date DATE,
                             return_date DATE,
                             PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE feedbacks (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    title LONGTEXT,
    content LONGTEXT,
    FOREIGN KEY (user_id) REFERENCES accounts(id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE book_category(
    book_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON UPDATE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

create table rent_report(
    id int primary key not null auto_increment,
    account_id int not null references accounts(id) on delete cascade,
    book_id int not null references books(id) on delete cascade,
    is_rent_operation boolean not null default false, -- true means rent operation, false means return!
    from_date date not null default (curdate()),
    to_date date -- not null if rent, null if return?
);

INSERT INTO accounts (is_admin, password, username,userFullname,phone_number, date_of_birth,email)
VALUES (b'1', 'admin', 'admin','Vũ Nguyễn Trường Minh','088888','2005-11-14','vutruongminh6d@gmail.com');

INSERT INTO accounts (is_admin, password, username,userFullname,phone_number, date_of_birth,email)
VALUES (b'1', 'admin1', 'admin1','Lê Sĩ Thái Sơn','088888','2005-11-14','vutruongminhr6d@gmail.com');