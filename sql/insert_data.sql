-- Optional sample-data seeder. Safe to run repeatedly after create_tables.sql.
-- init_database.sql already inserts these sample records; this script skips them there.
USE bookstore_db;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = 'admin@bookstore.com')
    INSERT INTO dbo.users (email, fullname, phone, passwd, signup_date, is_admin)
    VALUES ('admin@bookstore.com', N'Administrator', '123456789', 'admin123', SYSDATETIME(), 1);
IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = 'user1@gmail.com')
    INSERT INTO dbo.users (email, fullname, phone, passwd, signup_date, is_admin)
    VALUES ('user1@gmail.com', N'Nguyễn Văn A', '987654321', 'user123', SYSDATETIME(), 0);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.author WHERE author_name = N'Nguyễn Nhật Ánh')
    INSERT INTO dbo.author (author_name, date_of_birth) VALUES (N'Nguyễn Nhật Ánh', '1955-05-07');
IF NOT EXISTS (SELECT 1 FROM dbo.author WHERE author_name = N'Paulo Coelho')
    INSERT INTO dbo.author (author_name, date_of_birth) VALUES (N'Paulo Coelho', '1947-08-24');
IF NOT EXISTS (SELECT 1 FROM dbo.author WHERE author_name = N'Haruki Murakami')
    INSERT INTO dbo.author (author_name, date_of_birth) VALUES (N'Haruki Murakami', '1949-01-12');
GO

IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1001)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1001,N'Mắt Biếc',N'NXB Trẻ',85.00,N'Câu chuyện tình yêu đơn phương đầy xúc động','2019-01-01','mat-biec.jpg',100);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1002)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1002,N'Tôi Thấy Hoa Vàng Trên Cỏ Xanh',N'NXB Trẻ',78.00,N'Câu chuyện tuổi thơ miền quê','2010-06-15','hoa-vang.jpg',80);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1003)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1003,N'Cho Tôi Xin Một Vé Đi Tuổi Thơ',N'NXB Trẻ',65.00,N'Hoài niệm tuổi thơ tươi đẹp','2008-03-20','ve-tuoi-tho.jpg',60);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1004)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1004,N'Ngồi Khóc Trên Cây',N'NXB Trẻ',72.00,N'Chuyện tình lãng mạn','2013-08-01','ngoi-khoc.jpg',50);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1005)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1005,N'The Alchemist',N'HarperOne',120.00,N'A philosophical novel about following your dreams','1988-01-01','alchemist.jpg',200);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1006)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1006,N'Brida',N'HarperOne',95.00,N'A young womans journey of self-discovery','1990-05-10','brida.jpg',90);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1007)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1007,N'Eleven Minutes',N'HarperCollins',110.00,N'A story of sacred and profane love','2003-04-01','eleven-min.jpg',70);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1008)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1008,N'Norwegian Wood',N'Vintage',105.00,N'A nostalgic story of loss','1987-09-04','norwegian.jpg',150);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1009)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1009,N'Kafka on the Shore',N'Vintage',115.00,N'A metaphysical journey','2002-09-12','kafka.jpg',120);
IF NOT EXISTS (SELECT 1 FROM dbo.books WHERE isbn = 1010)
    INSERT INTO dbo.books (isbn,title,publisher,price,description,publish_date,cover_image,quantity)
    VALUES (1010,N'1Q84',N'Knopf',130.00,N'A dystopian love story in parallel worlds','2009-05-29','1q84.jpg',110);
GO

INSERT INTO dbo.book_author (bookid,author_id)
SELECT b.bookid,a.author_id FROM (VALUES
    (1001,N'Nguyễn Nhật Ánh'),(1002,N'Nguyễn Nhật Ánh'),(1003,N'Nguyễn Nhật Ánh'),(1004,N'Nguyễn Nhật Ánh'),
    (1005,N'Paulo Coelho'),(1006,N'Paulo Coelho'),(1007,N'Paulo Coelho'),
    (1008,N'Haruki Murakami'),(1009,N'Haruki Murakami'),(1010,N'Haruki Murakami')
) AS seed(isbn,author_name)
JOIN dbo.books b ON b.isbn=seed.isbn
JOIN dbo.author a ON a.author_name=seed.author_name
WHERE NOT EXISTS (SELECT 1 FROM dbo.book_author ba WHERE ba.bookid=b.bookid AND ba.author_id=a.author_id);
GO

INSERT INTO dbo.rating (userid,bookid,rating,review_text)
SELECT u.id,b.bookid,seed.rating,seed.review_text
FROM (VALUES
    ('user1@gmail.com',1001,5,N'Sách rất hay, đọc rất xúc động'),
    ('user1@gmail.com',1005,4,N'Must read for everyone!'),
    ('admin@bookstore.com',1001,4,N'Tác phẩm tuyệt vời của Nguyễn Nhật Ánh'),
    ('admin@bookstore.com',1008,5,N'Murakami viết hay quá')
) AS seed(email,isbn,rating,review_text)
JOIN dbo.users u ON u.email=seed.email
JOIN dbo.books b ON b.isbn=seed.isbn
WHERE NOT EXISTS (SELECT 1 FROM dbo.rating r WHERE r.userid=u.id AND r.bookid=b.bookid);
GO
