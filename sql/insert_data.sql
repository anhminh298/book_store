USE bookstore_db;

INSERT INTO users (email, fullname, phone, passwd, signup_date, is_admin)
VALUES ('admin@bookstore.com', N'Administrator', 123456789, 'admin123', GETDATE(), 1);

INSERT INTO users (email, fullname, phone, passwd, signup_date, is_admin)
VALUES ('user1@gmail.com', N'Nguyen Van A', 987654321, 'user123', GETDATE(), 0);

INSERT INTO author (author_name, date_of_birth) VALUES
(N'Nguyen Nhat Anh', '1955-05-07'),
(N'Paulo Coelho', '1947-08-24'),
(N'Haruki Murakami', '1949-01-12');

INSERT INTO books (isbn, title, publisher, price, description, publish_date, cover_image, quantity) VALUES
(1001, N'Mat Biec', N'NXB Tre', 85.00, N'Cau chuyen tinh yeu don phuong', '2019-01-01', 'mat-biec.jpg', 100),
(1002, N'Toi Thay Hoa Vang Tren Co Xanh', N'NXB Tre', 78.00, N'Cau chuyen tuoi tho mien que', '2010-06-15', 'hoa-vang.jpg', 80),
(1003, N'Cho Toi Xin Mot Ve Di Tuoi Tho', N'NXB Tre', 65.00, N'Hoai niem tuoi tho', '2008-03-20', 've-tuoi-tho.jpg', 60),
(1004, N'Ngoi Khoc Tren Cay', N'NXB Tre', 72.00, N'Chuyen tinh lang man', '2013-08-01', 'ngoi-khoc.jpg', 50),
(1005, N'The Alchemist', 'HarperOne', 120.00, N'A philosophical novel about following your dreams', '1988-01-01', 'alchemist.jpg', 200),
(1006, N'Brida', 'HarperOne', 95.00, N'A young womans journey of self-discovery', '1990-05-10', 'brida.jpg', 90),
(1007, N'Eleven Minutes', 'HarperCollins', 110.00, N'A story of sacred and profane love', '2003-04-01', 'eleven-min.jpg', 70),
(1008, N'Norwegian Wood', 'Vintage', 105.00, N'A nostalgic story of loss', '1987-09-04', 'norwegian.jpg', 150),
(1009, N'Kafka on the Shore', 'Vintage', 115.00, N'A metaphysical journey', '2002-09-12', 'kafka.jpg', 120),
(1010, N'1Q84', 'Knopf', 130.00, N'A dystopian love story in parallel worlds', '2009-05-29', '1q84.jpg', 110);

INSERT INTO book_author (bookid, author_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1),
(5, 2), (6, 2), (7, 2),
(8, 3), (9, 3), (10, 3);

INSERT INTO rating (userid, bookid, rating, review_text) VALUES
(2, 1, 5, N'Sach rat hay, doc rat xuc dong'),
(2, 5, 4, N'Must read for everyone!'),
(1, 1, 4, N'Tac pham tuyet voi'),
(1, 8, 5, N'Murakami viet hay qua');
