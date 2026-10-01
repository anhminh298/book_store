package com.nhm.bookstore.service;

import com.nhm.bookstore.dao.BookDAO_24162073;
import com.nhm.bookstore.dao.DBConnection_24162073;
import com.nhm.bookstore.dao.OrderDAO_24162073;
import com.nhm.bookstore.dao.OrderItemDAO_24162073;
import com.nhm.bookstore.model.Book_24162073;
import com.nhm.bookstore.model.OrderItem_24162073;
import com.nhm.bookstore.model.OrderStatus_24162073;
import com.nhm.bookstore.model.Order_24162073;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OrderService_24162073 {
    @FunctionalInterface
    interface ConnectionProvider {
        Connection open() throws SQLException;
    }

    private final ConnectionProvider connections;
    private final OrderDAO_24162073 orderDAO = new OrderDAO_24162073();
    private final OrderItemDAO_24162073 itemDAO = new OrderItemDAO_24162073();
    private final BookDAO_24162073 bookDAO = new BookDAO_24162073();

    public OrderService_24162073() {
        this(DBConnection_24162073::openConnection);
    }

    OrderService_24162073(ConnectionProvider connections) {
        this.connections = connections;
    }

    public int createOrder(int userId, Map<Integer, Integer> items, String receiverName,
                           String receiverPhone, String receiverEmail, String shippingAddress,
                           String note) throws SQLException {
        validateRequest(userId, items, receiverName, receiverPhone, receiverEmail,
                shippingAddress, note);

        Order_24162073 order = new Order_24162073();
        order.setUserId(userId);
        order.setReceiverName(receiverName.trim());
        order.setReceiverPhone(receiverPhone.trim());
        order.setReceiverEmail(receiverEmail.trim());
        order.setShippingAddress(shippingAddress.trim());
        order.setNote(note == null ? null : note.trim());

        // Stable lock order reduces deadlocks when two carts contain the same books.
        List<Integer> bookIds = new ArrayList<>(items.keySet());
        Collections.sort(bookIds);
        try (Connection conn = connections.open()) {
            conn.setAutoCommit(false);
            try {
                int orderId = orderDAO.insert(conn, order);
                BigDecimal total = BigDecimal.ZERO;
                for (int bookId : bookIds) {
                    int requested = items.get(bookId);
                    Book_24162073 book = bookDAO.findActiveById(conn, bookId);
                    if (book == null) {
                        throw new IllegalArgumentException("Book is unavailable: " + bookId);
                    }
                    if (book.getPrice() == null || book.getPrice().signum() < 0) {
                        throw new IllegalStateException("Book price is invalid: " + bookId);
                    }
                    if (bookDAO.decreaseQuantity(conn, bookId, requested) != 1) {
                        throw new IllegalStateException("Insufficient quantity for book: " + bookId);
                    }
                    OrderItem_24162073 item = new OrderItem_24162073();
                    item.setOrderId(orderId);
                    item.setBookId(bookId);
                    item.setBookTitle(book.getTitle());
                    item.setQuantity(requested);
                    item.setUnitPrice(book.getPrice());
                    itemDAO.insert(conn, item);
                    total = total.add(item.getSubtotal());
                }
                orderDAO.updateTotal(conn, orderId, total);
                conn.commit();
                return orderId;
            } catch (SQLException | RuntimeException e) {
                rollback(conn, e);
                throw e;
            }
        }
    }

    public List<Order_24162073> getOrdersByUser(int userId) throws SQLException {
        requirePositive(userId, "userId");
        try (Connection conn = connections.open()) {
            return orderDAO.findByUserId(conn, userId);
        }
    }

    /** Returns null for both missing orders and orders owned by another user. */
    public Order_24162073 getOrderDetailForUser(int orderId, int userId) throws SQLException {
        requirePositive(orderId, "orderId");
        requirePositive(userId, "userId");
        try (Connection conn = connections.open()) {
            Order_24162073 order = orderDAO.findByIdAndUserId(conn, orderId, userId);
            if (order != null) order.setItems(itemDAO.findByOrderId(conn, orderId));
            return order;
        }
    }

    public Order_24162073 getOrderDetailForAdmin(int adminUserId, int orderId)
            throws SQLException {
        requirePositive(orderId, "orderId");
        try (Connection conn = connections.open()) {
            requireAdmin(conn, adminUserId);
            Order_24162073 order = orderDAO.findById(conn, orderId);
            if (order != null) order.setItems(itemDAO.findByOrderId(conn, orderId));
            return order;
        }
    }

    /** A null status means all orders. */
    public List<Order_24162073> getAllOrders(int adminUserId, OrderStatus_24162073 status)
            throws SQLException {
        try (Connection conn = connections.open()) {
            requireAdmin(conn, adminUserId);
            return orderDAO.findAll(conn, status);
        }
    }

    public boolean cancelOrderByUser(int orderId, int userId) throws SQLException {
        requirePositive(orderId, "orderId");
        requirePositive(userId, "userId");
        try (Connection conn = connections.open()) {
            conn.setAutoCommit(false);
            try {
                if (orderDAO.cancelPendingOrder(conn, orderId, userId) != 1) {
                    conn.rollback();
                    return false;
                }
                restoreInventory(conn, orderId);
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                rollback(conn, e);
                throw e;
            }
        }
    }

    public boolean cancelOrderByAdmin(int adminUserId, int orderId) throws SQLException {
        requirePositive(orderId, "orderId");
        try (Connection conn = connections.open()) {
            conn.setAutoCommit(false);
            try {
                requireAdmin(conn, adminUserId);
                if (orderDAO.adminCancelOrder(conn, orderId) != 1) {
                    conn.rollback();
                    return false;
                }
                restoreInventory(conn, orderId);
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                rollback(conn, e);
                throw e;
            }
        }
    }

    /** Cancellation is handled by the two cancel methods because it restores inventory. */
    public boolean updateOrderStatus(int adminUserId, int orderId, OrderStatus_24162073 next)
            throws SQLException {
        requirePositive(orderId, "orderId");
        if (next == null || next == OrderStatus_24162073.CANCELLED) {
            throw new IllegalArgumentException("Use a cancel method for cancellation");
        }
        try (Connection conn = connections.open()) {
            conn.setAutoCommit(false);
            try {
                requireAdmin(conn, adminUserId);
                Order_24162073 current = orderDAO.findById(conn, orderId);
                if (current == null || !current.getStatus().canAdvanceTo(next)) {
                    conn.rollback();
                    return false;
                }
                if (orderDAO.updateStatus(conn, orderId, current.getStatus(), next) != 1) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                rollback(conn, e);
                throw e;
            }
        }
    }

    private void restoreInventory(Connection conn, int orderId) throws SQLException {
        List<OrderItem_24162073> items = itemDAO.findByOrderId(conn, orderId);
        if (items.isEmpty()) throw new SQLException("Order has no items: " + orderId);
        for (OrderItem_24162073 item : items) {
            if (bookDAO.increaseQuantity(conn, item.getBookId(), item.getQuantity()) != 1) {
                throw new SQLException("Inventory restore failed for book: " + item.getBookId());
            }
        }
    }

    private void requireAdmin(Connection conn, int adminUserId) throws SQLException {
        if (adminUserId <= 0 || !orderDAO.isAdmin(conn, adminUserId)) {
            throw new SecurityException("Admin access required");
        }
    }

    static void validateRequest(int userId, Map<Integer, Integer> items, String name,
                                String phone, String email, String address, String note) {
        requirePositive(userId, "userId");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("No order items");
        if (items.size() > 100) throw new IllegalArgumentException("Too many order items");
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            if (entry.getKey() == null || entry.getKey() <= 0
                    || entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Invalid book ID or quantity");
            }
        }
        requireText(name, 100, "receiverName");
        requireText(phone, 20, "receiverPhone");
        requireText(email, 255, "receiverEmail");
        requireText(address, 500, "shippingAddress");
        if (!email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Invalid receiverEmail");
        }
        if (note != null && note.length() > 500) {
            throw new IllegalArgumentException("note is too long");
        }
    }

    private static void requireText(String value, int maxLength, String field) {
        if (value == null || value.trim().isEmpty() || value.trim().length() > maxLength) {
            throw new IllegalArgumentException("Invalid " + field);
        }
    }

    private static void requirePositive(int value, String field) {
        if (value <= 0) throw new IllegalArgumentException("Invalid " + field);
    }

    private static void rollback(Connection conn, Exception original) {
        try {
            conn.rollback();
        } catch (SQLException rollbackError) {
            original.addSuppressed(rollbackError);
        }
    }
}
