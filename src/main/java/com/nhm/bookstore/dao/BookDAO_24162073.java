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
        String sql = "SELECT b.* FROM books b JOIN book_author ba ON b.bookid=ba.bookid WHERE ba.author_id=? ORDER BY b.bookid OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
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
        String sql = "SELECT COUNT(*) FROM books b JOIN book_author ba ON b.bookid=ba.bookid WHERE ba.author_id=?";
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
        String sql = "SELECT * FROM books WHERE bookid=?";
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
        List<Book_24162073> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY bookid OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
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
        String sql = "SELECT COUNT(*) FROM books";
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

    public void delete(int bookid) {
        String sql = "DELETE FROM books WHERE bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
        return book;
    }
}
