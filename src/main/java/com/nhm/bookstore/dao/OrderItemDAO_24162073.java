package com.nhm.bookstore.dao;

import com.nhm.bookstore.model.OrderItem_24162073;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO_24162073 {
    public void insert(Connection conn, OrderItem_24162073 item) throws SQLException {
        String sql = "INSERT INTO order_items (orderid,bookid,book_title,quantity,unit_price)"
                + " VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getBookId());
            ps.setString(3, item.getBookTitle());
            ps.setInt(4, item.getQuantity());
            ps.setBigDecimal(5, item.getUnitPrice());
            if (ps.executeUpdate() != 1) throw new SQLException("Order item insert failed");
        }
    }

    public List<OrderItem_24162073> findByOrderId(Connection conn, int orderId)
            throws SQLException {
        List<OrderItem_24162073> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM order_items WHERE orderid=? ORDER BY order_item_id")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem_24162073 item = new OrderItem_24162073();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("orderid"));
                    item.setBookId(rs.getInt("bookid"));
                    item.setBookTitle(rs.getString("book_title"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    items.add(item);
                }
            }
        }
        return items;
    }
}
