package com.nhm.bookstore.dao;

import com.nhm.bookstore.model.User_24162073;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAO_24162073 {
    public void register(User_24162073 user) {
        String sql = "INSERT INTO users (email, fullname, phone, passwd, signup_date, is_admin) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullname());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPasswd());
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User_24162073 findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User_24162073 login(String email, String passwd) {
        String sql = "SELECT * FROM users WHERE email=? AND passwd=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login=GETDATE() WHERE id=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User_24162073 findById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection conn = DBConnection_24162073.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User_24162073 extractUser(ResultSet rs) throws SQLException {
        User_24162073 user = new User_24162073();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setFullname(rs.getString("fullname"));
        user.setPhone(rs.getString("phone"));
        user.setPasswd(rs.getString("passwd"));
        user.setSignupDate(rs.getTimestamp("signup_date"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setAdmin(rs.getBoolean("is_admin"));
        return user;
    }
}
