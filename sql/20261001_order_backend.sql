-- Run once after init_database.sql on an existing bookstore_db.
-- Existing INT phone values are converted to text; any leading zeros lost earlier cannot be recovered.
USE bookstore_db;
GO

IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.users')
           AND name = N'phone' AND system_type_id = TYPE_ID(N'int'))
    ALTER TABLE dbo.users ALTER COLUMN phone VARCHAR(20) NULL;
GO

ALTER TABLE dbo.books ALTER COLUMN price DECIMAL(18,2) NULL;
GO

IF COL_LENGTH('dbo.books', 'is_active') IS NULL
    ALTER TABLE dbo.books ADD is_active BIT NOT NULL
        CONSTRAINT DF_books_is_active DEFAULT (1) WITH VALUES;
GO

IF OBJECT_ID(N'dbo.orders', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.orders (
        orderid INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        userid INT NOT NULL,
        receiver_name NVARCHAR(100) NOT NULL,
        receiver_phone VARCHAR(20) NOT NULL,
        receiver_email VARCHAR(255) NOT NULL,
        shipping_address NVARCHAR(500) NOT NULL,
        note NVARCHAR(500) NULL,
        total_amount DECIMAL(18,2) NOT NULL,
        payment_method VARCHAR(20) NOT NULL CONSTRAINT DF_orders_payment DEFAULT ('COD'),
        status VARCHAR(20) NOT NULL CONSTRAINT DF_orders_status DEFAULT ('PENDING'),
        created_at DATETIME2 NOT NULL CONSTRAINT DF_orders_created DEFAULT (SYSDATETIME()),
        updated_at DATETIME2 NULL,
        CONSTRAINT FK_orders_users FOREIGN KEY (userid) REFERENCES dbo.users(id),
        CONSTRAINT CK_orders_total CHECK (total_amount >= 0),
        CONSTRAINT CK_orders_payment CHECK (payment_method = 'COD'),
        CONSTRAINT CK_orders_status CHECK (status IN
            ('PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED'))
    );
END;
GO

IF OBJECT_ID(N'dbo.order_items', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_items (
        order_item_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        orderid INT NOT NULL,
        bookid INT NOT NULL,
        book_title NVARCHAR(255) NOT NULL,
        quantity INT NOT NULL,
        unit_price DECIMAL(18,2) NOT NULL,
        CONSTRAINT FK_order_items_orders FOREIGN KEY (orderid) REFERENCES dbo.orders(orderid),
        CONSTRAINT FK_order_items_books FOREIGN KEY (bookid) REFERENCES dbo.books(bookid),
        CONSTRAINT CK_order_items_quantity CHECK (quantity > 0),
        CONSTRAINT CK_order_items_price CHECK (unit_price >= 0)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID(N'dbo.orders')
               AND name = N'IX_orders_user_created')
    CREATE INDEX IX_orders_user_created ON dbo.orders(userid, created_at DESC);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID(N'dbo.order_items')
               AND name = N'IX_order_items_order')
    CREATE INDEX IX_order_items_order ON dbo.order_items(orderid);
GO
