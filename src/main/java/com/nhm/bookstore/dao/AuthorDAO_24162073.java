package com.nhm.bookstore.dao;

import com.nhm.bookstore.model.Author_24162073;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO_24162073 {
    public List<Author_24162073> findAll() {
        List<Author_24162073> authors = new ArrayList<>();
        String sql = "SELECT * FROM author ORDER BY author_name";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                authors.add(extractAuthor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    public Author_24162073 findById(int authorId) {
        String sql = "SELECT * FROM author WHERE author_id=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAuthor(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Author_24162073> findByBookId(int bookid) {
        List<Author_24162073> authors = new ArrayList<>();
        String sql = "SELECT a.* FROM author a JOIN book_author ba ON a.author_id=ba.author_id WHERE ba.bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    authors.add(extractAuthor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    private Author_24162073 extractAuthor(ResultSet rs) throws SQLException {
        Author_24162073 author = new Author_24162073();
        author.setAuthorId(rs.getInt("author_id"));
        author.setAuthorName(rs.getString("author_name"));
        author.setDateOfBirth(rs.getDate("date_of_birth"));
        return author;
    }
}
