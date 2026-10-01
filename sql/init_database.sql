-- ============================================
-- BookStore Database - 24162073_NguyenAnhMinh
-- SQL Server - Đề thi Quá trình - Đề số 02
-- ============================================

CREATE DATABASE bookstore_db;
GO
USE bookstore_db;
GO

-- Bảng users
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

-- Bảng books
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

-- Bảng author
CREATE TABLE author (
    author_id INT IDENTITY(1,1) PRIMARY KEY,
    author_name NVARCHAR(100),
    date_of_birth DATE
);

-- Bảng book_author (junction table - many-to-many)
CREATE TABLE book_author (
    bookid INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (bookid, author_id),
    FOREIGN KEY (bookid) REFERENCES books(bookid),
    FOREIGN KEY (author_id) REFERENCES author(author_id)
);

-- Bảng rating
CREATE TABLE rating (
    userid INT NOT NULL,
    bookid INT NOT NULL,
    rating TINYINT NOT NULL CONSTRAINT CK_rating_range CHECK (rating BETWEEN 1 AND 5),
    review_text NVARCHAR(MAX),
    PRIMARY KEY (userid, bookid),
    FOREIGN KEY (userid) REFERENCES users(id),
    FOREIGN KEY (bookid) REFERENCES books(bookid)
);

-- ========== DỮ LIỆU TEST ==========

-- Admin account (passwd: admin123)
INSERT INTO users (email, fullname, phone, passwd, signup_date, is_admin)
VALUES ('admin@bookstore.com', N'Administrator', 123456789, 'admin123', GETDATE(), 1);

-- User account (passwd: user123)
INSERT INTO users (email, fullname, phone, passwd, signup_date, is_admin)
VALUES ('user1@gmail.com', N'Nguyễn Văn A', 987654321, 'user123', GETDATE(), 0);

-- Authors
INSERT INTO author (author_name, date_of_birth) VALUES
(N'Nguyễn Nhật Ánh', '1955-05-07'),
(N'Paulo Coelho', '1947-08-24'),
(N'Haruki Murakami', '1949-01-12');

-- Books (10 cuốn để test phân trang 3sp/trang)
INSERT INTO books (isbn, title, publisher, price, description, publish_date, cover_image, quantity) VALUES
(1001, N'Mắt Biếc', N'NXB Trẻ', 85.00, N'Câu chuyện tình yêu đơn phương đầy xúc động', '2019-01-01', 'mat-biec.jpg', 100),
(1002, N'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', N'NXB Trẻ', 78.00, N'Câu chuyện tuổi thơ miền quê', '2010-06-15', 'hoa-vang.jpg', 80),
(1003, N'Cho Tôi Xin Một Vé Đi Tuổi Thơ', N'NXB Trẻ', 65.00, N'Hoài niệm tuổi thơ tươi đẹp', '2008-03-20', 've-tuoi-tho.jpg', 60),
(1004, N'Ngồi Khóc Trên Cây', N'NXB Trẻ', 72.00, N'Chuyện tình lãng mạn', '2013-08-01', 'ngoi-khoc.jpg', 50),
(1005, N'The Alchemist', 'HarperOne', 120.00, N'A philosophical novel about following your dreams', '1988-01-01', 'alchemist.jpg', 200),
(1006, N'Brida', 'HarperOne', 95.00, N'A young womans journey of self-discovery', '1990-05-10', 'brida.jpg', 90),
(1007, N'Eleven Minutes', 'HarperCollins', 110.00, N'A story of sacred and profane love', '2003-04-01', 'eleven-min.jpg', 70),
(1008, N'Norwegian Wood', 'Vintage', 105.00, N'A nostalgic story of loss and sexuality', '1987-09-04', 'norwegian.jpg', 150),
(1009, N'Kafka on the Shore', 'Vintage', 115.00, N'A metaphysical journey of two characters', '2002-09-12', 'kafka.jpg', 120),
(1010, N'1Q84', 'Knopf', 130.00, N'A dystopian love story in parallel worlds', '2009-05-29', '1q84.jpg', 110);

-- Book-Author relationships (many-to-many)
INSERT INTO book_author (bookid, author_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1),   -- Nguyễn Nhật Ánh: 4 cuốn
(5, 2), (6, 2), (7, 2),            -- Paulo Coelho: 3 cuốn
(8, 3), (9, 3), (10, 3);           -- Haruki Murakami: 3 cuốn

-- Ratings/Reviews
INSERT INTO rating (userid, bookid, rating, review_text) VALUES
(2, 1, 5, N'Sách rất hay, đọc rất xúc động'),
(2, 5, 4, N'Must read for everyone!'),
(1, 1, 4, N'Tác phẩm tuyệt vời của Nguyễn Nhật Ánh'),
(1, 8, 5, N'Murakami viết hay quá');
GO
