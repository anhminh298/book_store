package com.nhm.bookstore.dao;

import com.nhm.bookstore.model.Book_24162073;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDAO_24162073 {
    
    public List<Book_24162073> findByAuthorId(int authorId, int page, int pageSize) {
        List<Book_24162073> books = new ArrayList<>();
        String sql = "SELECT b.* FROM books b JOIN book_author ba ON b.bookid=ba.bookid WHERE ba.author_id=? AND b.is_active=1 ORDER BY b.bookid OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBook(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public int countByAuthorId(int authorId) {
        String sql = "SELECT COUNT(*) FROM books b JOIN book_author ba ON b.bookid=ba.bookid WHERE ba.author_id=? AND b.is_active=1";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Book_24162073 findById(int bookid) {
        String sql = "SELECT * FROM books WHERE bookid=? AND is_active=1";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBook(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book_24162073> findAll(int page, int pageSize) {
        return findAllInternal(page, pageSize, false);
    }

    public List<Book_24162073> findAllForAdmin(int page, int pageSize) {
        return findAllInternal(page, pageSize, true);
    }

    private List<Book_24162073> findAllInternal(int page, int pageSize, boolean includeInactive) {
        List<Book_24162073> books = new ArrayList<>();
        String sql = "SELECT * FROM books " + (includeInactive ? "" : "WHERE is_active=1 ")
                + "ORDER BY bookid OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBook(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public int countAll() {
        return countAllInternal(false);
    }

    public int countAllForAdmin() {
        return countAllInternal(true);
    }

    private int countAllInternal(boolean includeInactive) {
        String sql = "SELECT COUNT(*) FROM books" + (includeInactive ? "" : " WHERE is_active=1");
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int insert(Book_24162073 book) {
        String sql = "INSERT INTO books (isbn, title, publisher, price, description, publish_date, cover_image, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getPublisher());
            ps.setBigDecimal(4, book.getPrice());
            ps.setString(5, book.getDescription());
            ps.setDate(6, book.getPublishDate());
            ps.setString(7, book.getCoverImage());
            ps.setInt(8, book.getQuantity());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void update(Book_24162073 book) {
        String sql = "UPDATE books SET isbn=?, title=?, publisher=?, price=?, description=?, publish_date=?, cover_image=?, quantity=? WHERE bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getPublisher());
            ps.setBigDecimal(4, book.getPrice());
            ps.setString(5, book.getDescription());
            ps.setDate(6, book.getPublishDate());
            ps.setString(7, book.getCoverImage());
            ps.setInt(8, book.getQuantity());
            ps.setInt(9, book.getBookid());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int bookid) {
        try (Connection conn = DBConnection_24162073.openConnection()) {
            return softDelete(conn, bookid) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int softDelete(Connection conn, int bookid) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE books SET is_active=0 WHERE bookid=? AND is_active=1")) {
            ps.setInt(1, bookid);
            return ps.executeUpdate();
        }
    }

    public Book_24162073 findByIdForAdmin(int bookId) {
        String sql = "SELECT * FROM books WHERE bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extractBook(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Locks the current row through checkout to keep title and price in the same snapshot. */
    public Book_24162073 findActiveById(Connection conn, int bookId) throws SQLException {
        String sql = "SELECT * FROM books WITH (UPDLOCK, HOLDLOCK) WHERE bookid=? AND is_active=1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extractBook(rs) : null;
            }
        }
    }

    public int decreaseQuantity(Connection conn, int bookId, int quantity) throws SQLException {
        if (bookId <= 0 || quantity <= 0) throw new IllegalArgumentException("Invalid quantity");
        String sql = "UPDATE books SET quantity=quantity-? WHERE bookid=? AND quantity>=? AND is_active=1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, bookId);
            ps.setInt(3, quantity);
            return ps.executeUpdate();
        }
    }

    public int increaseQuantity(Connection conn, int bookId, int quantity) throws SQLException {
        if (bookId <= 0 || quantity <= 0) throw new IllegalArgumentException("Invalid quantity");
        String sql = "UPDATE books SET quantity=quantity+? WHERE bookid=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, bookId);
            return ps.executeUpdate();
        }
    }

    private Book_24162073 extractBook(ResultSet rs) throws SQLException {
        Book_24162073 book = new Book_24162073();
        book.setBookid(rs.getInt("bookid"));
        book.setIsbn(rs.getInt("isbn"));
        book.setTitle(rs.getString("title"));
        book.setPublisher(rs.getString("publisher"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setDescription(rs.getString("description"));
        book.setPublishDate(rs.getDate("publish_date"));
        book.setCoverImage(rs.getString("cover_image"));
        book.setQuantity(rs.getInt("quantity"));
        book.setActive(rs.getBoolean("is_active"));
        return book;
    }
}
