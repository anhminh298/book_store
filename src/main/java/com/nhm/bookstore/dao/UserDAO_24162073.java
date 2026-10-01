package com.nhm.bookstore.dao;

import com.nhm.bookstore.model.User_24162073;
import com.nhm.bookstore.util.PasswordUtil_24162073;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAO_24162073 {
    public boolean register(User_24162073 user, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (email, fullname, phone, passwd, signup_date, is_admin) "
                + "VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection_24162073.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullname());
            ps.setString(3, user.getPhone());
            ps.setString(4, passwordHash);
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate() == 1;
        }
    }

    public User_24162073 findByEmail(String email) throws SQLException {
        String sql = "SELECT id,email,fullname,phone,signup_date,last_login,is_admin "
                + "FROM users WHERE email=?";
        try (Connection conn = DBConnection_24162073.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extractUser(rs) : null;
            }
        }
    }

    public User_24162073 login(String email, String password) throws SQLException {
        String sql = "SELECT id,email,fullname,phone,passwd,signup_date,last_login,is_admin "
                + "FROM users WHERE email=?";
        try (Connection conn = DBConnection_24162073.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            String storedPassword;
            User_24162073 user;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                storedPassword = rs.getString("passwd");
                if (!PasswordUtil_24162073.verify(password, storedPassword)) return null;
                user = extractUser(rs);
            }
            if (PasswordUtil_24162073.needsRehash(storedPassword)) {
                String upgradeSql = "UPDATE users SET passwd=? WHERE id=? AND passwd=?";
                try (PreparedStatement upgrade = conn.prepareStatement(upgradeSql)) {
                    upgrade.setString(1, PasswordUtil_24162073.hash(password));
                    upgrade.setInt(2, user.getId());
                    upgrade.setString(3, storedPassword);
                    upgrade.executeUpdate();
                }
            }
            return user;
        }
    }

    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_login=GETDATE() WHERE id=?";
        try (Connection conn = DBConnection_24162073.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT id FROM users WHERE email=?";
        try (Connection conn = DBConnection_24162073.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User_24162073 findById(int id) throws SQLException {
        String sql = "SELECT id,email,fullname,phone,signup_date,last_login,is_admin "
                + "FROM users WHERE id=?";
        try (Connection conn = DBConnection_24162073.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extractUser(rs) : null;
            }
        }
    }

    private User_24162073 extractUser(ResultSet rs) throws SQLException {
        User_24162073 user = new User_24162073();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setFullname(rs.getString("fullname"));
        user.setPhone(rs.getString("phone"));
        user.setSignupDate(rs.getTimestamp("signup_date"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setAdmin(rs.getBoolean("is_admin"));
        return user;
    }
}
