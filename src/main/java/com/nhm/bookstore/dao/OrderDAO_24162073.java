package com.nhm.bookstore.dao;

import com.nhm.bookstore.model.OrderStatus_24162073;
import com.nhm.bookstore.model.Order_24162073;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO_24162073 {
    public int insert(Connection conn, Order_24162073 order) throws SQLException {
        String sql = "INSERT INTO orders (userid,receiver_name,receiver_phone,receiver_email,"
                + "shipping_address,note,total_amount,payment_method,status) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setString(2, order.getReceiverName());
            ps.setString(3, order.getReceiverPhone());
            ps.setString(4, order.getReceiverEmail());
            ps.setString(5, order.getShippingAddress());
            ps.setString(6, order.getNote());
            ps.setBigDecimal(7, BigDecimal.ZERO);
            ps.setString(8, "COD");
            ps.setString(9, OrderStatus_24162073.PENDING.name());
            if (ps.executeUpdate() != 1) throw new SQLException("Order insert did not affect one row");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Order ID was not generated");
    }

    public void updateTotal(Connection conn, int orderId, BigDecimal total) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET total_amount=? WHERE orderid=?")) {
            ps.setBigDecimal(1, total);
            ps.setInt(2, orderId);
            if (ps.executeUpdate() != 1) throw new SQLException("Order total update failed");
        }
    }

    public Order_24162073 findByIdAndUserId(Connection conn, int orderId, int userId)
            throws SQLException {
        String sql = "SELECT o.*,u.email AS user_email,u.fullname AS user_fullname "
                + "FROM orders o JOIN users u ON u.id=o.userid WHERE o.orderid=? AND o.userid=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Order_24162073 findById(Connection conn, int orderId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*,u.email AS user_email,u.fullname AS user_fullname "
                + "FROM orders o JOIN users u ON u.id=o.userid WHERE o.orderid=?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Order_24162073> findByUserId(Connection conn, int userId) throws SQLException {
        List<Order_24162073> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT o.*,u.email AS user_email,u.fullname AS user_fullname "
                + "FROM orders o JOIN users u ON u.id=o.userid "
                + "WHERE o.userid=? ORDER BY o.created_at DESC,o.orderid DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(map(rs));
            }
        }
        return orders;
    }

    public List<Order_24162073> findAll(Connection conn, OrderStatus_24162073 status)
            throws SQLException {
        List<Order_24162073> orders = new ArrayList<>();
        String sql = "SELECT o.*,u.email AS user_email,u.fullname AS user_fullname "
                + "FROM orders o JOIN users u ON u.id=o.userid"
                + (status == null ? "" : " WHERE o.status=?")
                + " ORDER BY o.created_at DESC,o.orderid DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null) ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(map(rs));
            }
        }
        return orders;
    }

    public int cancelPendingOrder(Connection conn, int orderId, int userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET status='CANCELLED',updated_at=SYSDATETIME() "
                + "WHERE orderid=? AND userid=? AND status='PENDING'")) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            return ps.executeUpdate();
        }
    }

    public int adminCancelOrder(Connection conn, int orderId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET status='CANCELLED',updated_at=SYSDATETIME() "
                + "WHERE orderid=? AND status IN ('PENDING','CONFIRMED')")) {
            ps.setInt(1, orderId);
            return ps.executeUpdate();
        }
    }

    public int updateStatus(Connection conn, int orderId, OrderStatus_24162073 current,
                            OrderStatus_24162073 next) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET status=?,updated_at=SYSDATETIME() WHERE orderid=? AND status=?")) {
            ps.setString(1, next.name());
            ps.setInt(2, orderId);
            ps.setString(3, current.name());
            return ps.executeUpdate();
        }
    }

    public boolean isAdmin(Connection conn, int userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT is_admin FROM users WHERE id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    private Order_24162073 map(ResultSet rs) throws SQLException {
        Order_24162073 order = new Order_24162073();
        order.setOrderId(rs.getInt("orderid"));
        order.setUserId(rs.getInt("userid"));
        order.setUserEmail(rs.getString("user_email"));
        order.setUserFullname(rs.getString("user_fullname"));
        order.setReceiverName(rs.getString("receiver_name"));
        order.setReceiverPhone(rs.getString("receiver_phone"));
        order.setReceiverEmail(rs.getString("receiver_email"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setNote(rs.getString("note"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setStatus(OrderStatus_24162073.valueOf(rs.getString("status")));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        return order;
    }
}
