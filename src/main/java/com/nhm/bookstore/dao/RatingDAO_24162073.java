package com.nhm.bookstore.dao;

import com.nhm.bookstore.model.Rating_24162073;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO_24162073 {
    public List<Rating_24162073> findByBookId(int bookid) {
        List<Rating_24162073> ratings = new ArrayList<>();
        String sql = "SELECT r.*, u.fullname as userFullname FROM rating r JOIN users u ON r.userid=u.id WHERE r.bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rating_24162073 rating = new Rating_24162073();
                    rating.setUserid(rs.getInt("userid"));
                    rating.setBookid(rs.getInt("bookid"));
                    rating.setRating(rs.getInt("rating"));
                    rating.setReviewText(rs.getString("review_text"));
                    rating.setUserFullname(rs.getString("userFullname"));
                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public int countByBookId(int bookid) {
        String sql = "SELECT COUNT(*) FROM rating WHERE bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
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

    public void insert(Rating_24162073 rating) {
        String sql = "INSERT INTO rating(userid, bookid, rating, review_text) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating.getUserid());
            ps.setInt(2, rating.getBookid());
            ps.setInt(3, rating.getRating());
            ps.setString(4, rating.getReviewText());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(int userid, int bookid) {
        String sql = "SELECT COUNT(*) FROM rating WHERE userid=? AND bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userid);
            ps.setInt(2, bookid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void update(Rating_24162073 rating) {
        String sql = "UPDATE rating SET rating=?, review_text=? WHERE userid=? AND bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating.getRating());
            ps.setString(2, rating.getReviewText());
            ps.setInt(3, rating.getUserid());
            ps.setInt(4, rating.getBookid());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByBookId(int bookid) {
        String sql = "DELETE FROM rating WHERE bookid=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
