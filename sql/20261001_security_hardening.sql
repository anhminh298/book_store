-- Run once after 20261001_order_backend.sql; safe to re-run.
USE bookstore_db;
GO

IF COL_LENGTH('dbo.users', 'email') IS NOT NULL
    ALTER TABLE dbo.users ALTER COLUMN email VARCHAR(255) NOT NULL;
GO

IF COL_LENGTH('dbo.users', 'passwd') IS NOT NULL
    ALTER TABLE dbo.users ALTER COLUMN passwd VARCHAR(255) NOT NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID(N'dbo.users')
               AND name = N'UX_users_email')
    CREATE UNIQUE INDEX UX_users_email ON dbo.users(email);
GO

IF EXISTS (SELECT 1 FROM dbo.rating WHERE rating < 1 OR rating > 5)
    THROW 50002, 'Resolve ratings outside 1 to 5 before migration.', 1;
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints
               WHERE parent_object_id = OBJECT_ID(N'dbo.rating') AND name = N'CK_rating_range')
    ALTER TABLE dbo.rating ADD CONSTRAINT CK_rating_range CHECK (rating BETWEEN 1 AND 5);
GO
