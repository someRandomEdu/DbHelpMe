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
   publisher VARCHAR(255) NOT NULL DEFAULT '',
   description VARCHAR(255) NOT NULL DEFAULT '',
   category_id INT,
   PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

Create table authors (
  author_id int auto_increment primary key,
  author_name varchar(255)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE book_author (
    book_id INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE
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

CREATE TABLE wishlist (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT NOT NULL,
                          book_id INT NOT NULL,
                          added_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES accounts(id) ON DELETE CASCADE,
                          FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
                          UNIQUE KEY (user_id, book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE notifications (
                               id INT NOT NULL AUTO_INCREMENT,
                               user_id INT NOT NULL,
                               message TEXT NOT NULL,
                               type VARCHAR(50) NOT NULL DEFAULT 'general',
                               status VARCHAR(20) DEFAULT 'unread',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               FOREIGN KEY (user_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO accounts (is_admin, password, username,userFullname,phone_number, date_of_birth,email)
VALUES (b'1', 'admin', 'admin','Vũ Nguyễn Trường Minh','088888','2005-11-14','vutruongminh6d@gmail.com');

INSERT INTO accounts (is_admin, password, username,userFullname,phone_number, date_of_birth,email)
VALUES (b'1', 'admin1', 'admin1','Lê Sĩ Thái Sơn','088888','2005-11-14','vutruongminhr6d@gmail.com');

-- Insert authors
INSERT INTO authors (author_name) VALUES
('Harper Lee'),
('George Orwell'),
('Jane Austen'),
('F. Scott Fitzgerald'),
('Herman Melville'),
('Leo Tolstoy'),
('J.D. Salinger'),
('J.R.R. Tolkien'),
('Paulo Coelho'),
('Markus Zusak');

-- Insert books
INSERT INTO books (title, publisher, description, category_id) VALUES
('To Kill a Mockingbird', 'J.B. Lippincott & Co.', 'A novel about racial injustice.', NULL),
('1984', 'Secker & Warburg', 'A dystopian novel about totalitarianism.', NULL),
('Pride and Prejudice', 'T. Egerton', 'A romantic novel of manners.', NULL),
('The Great Gatsby', 'Charles Scribner\'s Sons', 'A novel about the American dream.', NULL),
('Moby Dick', 'Harper & Brothers', 'A novel about a giant white whale.', NULL),
('War and Peace', 'The Russian Messenger', 'A novel about Napoleon\'s invasion of Russia.', NULL),
('The Catcher in the Rye', 'Little, Brown and Company', 'A story about teenage rebellion.', NULL),
('The Hobbit', 'George Allen & Unwin', 'A fantasy novel about a hobbit\'s adventure.', NULL),
('The Alchemist', 'HarperOne', 'A novel about following one\'s dreams.', NULL),
('The Book Thief', 'Picador', 'A story set in Nazi Germany.', NULL);

-- Link books and authors in book_author
INSERT INTO book_author (book_id, author_id) VALUES
(1, 1), -- To Kill a Mockingbird -> Harper Lee
(2, 2), -- 1984 -> George Orwell
(3, 3), -- Pride and Prejudice -> Jane Austen
(4, 4), -- The Great Gatsby -> F. Scott Fitzgerald
(5, 5), -- Moby Dick -> Herman Melville
(6, 6), -- War and Peace -> Leo Tolstoy
(7, 7), -- The Catcher in the Rye -> J.D. Salinger
(8, 8), -- The Hobbit -> J.R.R. Tolkien
(9, 9), -- The Alchemist -> Paulo Coelho
(10, 10); -- The Book Thief -> Markus Zusak
