USE bookstore_db;

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    fullname NVARCHAR(50),
    phone INT,
    passwd VARCHAR(32),
    signup_date DATETIME,
    last_login DATETIME,
    is_admin BIT
);

CREATE TABLE books (
    bookid INT IDENTITY(1,1) PRIMARY KEY,
    isbn INT,
    title VARCHAR(200),
    publisher VARCHAR(100),
    price DECIMAL(6,2),
    description TEXT,
    publish_date DATE,
    cover_image VARCHAR(100),
    quantity INT
);

CREATE TABLE author (
    author_id INT IDENTITY(1,1) PRIMARY KEY,
    author_name VARCHAR(100),
    date_of_birth DATE
);

CREATE TABLE book_author (
    bookid INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (bookid, author_id),
    FOREIGN KEY (bookid) REFERENCES books(bookid),
    FOREIGN KEY (author_id) REFERENCES author(author_id)
);

CREATE TABLE rating (
    userid INT NOT NULL,
    bookid INT NOT NULL,
    rating TINYINT,
    review_text TEXT,
    PRIMARY KEY (userid, bookid),
    FOREIGN KEY (userid) REFERENCES users(id),
    FOREIGN KEY (bookid) REFERENCES books(bookid)
);
