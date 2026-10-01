package com.nhm.bookstore.dao;

import com.nhm.bookstore.model.BookAuthor_24162073;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookAuthorDAO_24162073 {
    public void insert(int bookid, int authorId) {
        String sql = "INSERT INTO book_author (bookid, author_id) VALUES (?, ?)";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            ps.setInt(2, authorId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByBookId(int bookid) {
        String sql = "DELETE FROM book_author WHERE bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BookAuthor_24162073> findByBookId(int bookid) {
        List<BookAuthor_24162073> bookAuthors = new ArrayList<>();
        String sql = "SELECT * FROM book_author WHERE bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookAuthor_24162073 ba = new BookAuthor_24162073();
                    ba.setBookid(rs.getInt("bookid"));
                    ba.setAuthorId(rs.getInt("author_id"));
                    bookAuthors.add(ba);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookAuthors;
    }
}
