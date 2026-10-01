USE bookstore_db;

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    fullname NVARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    passwd VARCHAR(255) NOT NULL,
    signup_date DATETIME2 NOT NULL CONSTRAINT DF_users_signup_date DEFAULT (SYSDATETIME()),
    last_login DATETIME2,
    is_admin BIT NOT NULL CONSTRAINT DF_users_is_admin DEFAULT (0)
);

CREATE TABLE books (
    bookid INT IDENTITY(1,1) PRIMARY KEY,
    isbn INT,
    title NVARCHAR(200),
    publisher NVARCHAR(100),
    price DECIMAL(18,2),
    description NVARCHAR(MAX),
    publish_date DATE,
    cover_image VARCHAR(255),
    quantity INT NOT NULL CONSTRAINT CK_books_quantity CHECK (quantity >= 0),
    is_active BIT NOT NULL CONSTRAINT DF_books_is_active DEFAULT (1)
);

CREATE TABLE author (
    author_id INT IDENTITY(1,1) PRIMARY KEY,
    author_name NVARCHAR(100),
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
    rating TINYINT NOT NULL CONSTRAINT CK_rating_range CHECK (rating BETWEEN 1 AND 5),
    review_text NVARCHAR(MAX),
    PRIMARY KEY (userid, bookid),
    FOREIGN KEY (userid) REFERENCES users(id),
    FOREIGN KEY (bookid) REFERENCES books(bookid)
);
