package com.nhm.bookstore.service;

import com.nhm.bookstore.dao.DBConnection_24162073;
import com.nhm.bookstore.dao.BookDAO_24162073;
import com.nhm.bookstore.model.OrderStatus_24162073;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "BOOKSTORE_SQL_INTEGRATION", matches = "true")
class OrderServiceSqlServerTest {
    private static String database;
    private static OrderService_24162073 service;

    @BeforeAll
    static void createDatabase() throws Exception {
        database = "bookstore_backend_it_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        try (Connection conn = DBConnection_24162073.openConnection();
             Statement statement = conn.createStatement()) {
            statement.execute("CREATE DATABASE [" + database + "]");
            conn.setCatalog(database);
            String base = Files.readString(Path.of("sql/create_tables.sql"), StandardCharsets.UTF_8)
                    .replaceFirst("(?i)USE bookstore_db;", "");
            statement.execute(base);
            statement.execute("INSERT INTO users(email,fullname,phone,passwd,is_admin) "
                    + "VALUES ('legacy@example.com','Legacy',123456789,'test',0)");
            statement.execute("INSERT INTO books(isbn,title,publisher,price,quantity) "
                    + "VALUES (9090,'Legacy Book','Legacy Publisher',19.95,2)");
            String migration = Files.readString(Path.of("sql/20261001_order_backend.sql"),
                    StandardCharsets.UTF_8).replaceFirst("(?i)USE bookstore_db;", "");
            for (String batch : migration.split("(?im)^GO\\s*$")) {
                if (!batch.isBlank()) statement.execute(batch);
            }
        }
        service = new OrderService_24162073(() -> {
            Connection conn = DBConnection_24162073.openConnection();
            conn.setCatalog(database);
            return conn;
        });
    }

    @AfterAll
    static void dropDatabase() throws Exception {
        if (database == null) return;
        try (Connection conn = DBConnection_24162073.openConnection();
             Statement statement = conn.createStatement()) {
            statement.execute("ALTER DATABASE [" + database + "] SET SINGLE_USER WITH ROLLBACK IMMEDIATE");
            statement.execute("DROP DATABASE [" + database + "]");
        }
    }

    @Test
    void migrationPreservesLegacyRowsAndAddsQuantityConstraint() throws Exception {
        try (Connection conn = connection();
             Statement statement = conn.createStatement();
             ResultSet rows = statement.executeQuery(
                     "SELECT phone FROM users WHERE email='legacy@example.com'")) {
            assertTrue(rows.next());
            assertEquals("123456789", rows.getString(1));
        }
        try (Connection conn = connection();
             Statement statement = conn.createStatement();
             ResultSet rows = statement.executeQuery(
                     "SELECT price,quantity,is_active FROM books WHERE isbn=9090")) {
            assertTrue(rows.next());
            assertEquals(new BigDecimal("19.95"), rows.getBigDecimal(1));
            assertEquals(2, rows.getInt(2));
            assertTrue(rows.getBoolean(3));
        }
        try (Connection conn = connection(); Statement statement = conn.createStatement()) {
            assertThrows(SQLException.class,
                    () -> statement.executeUpdate("UPDATE books SET quantity=-1 WHERE isbn=9090"));
        }
    }

    @Test
    void onlyOneConcurrentCheckoutCanTakeLastCopy() throws Exception {
        int user = createUser(false);
        int book = createBook(1);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> first = pool.submit(() -> checkoutAfter(start, user, book));
            Future<Boolean> second = pool.submit(() -> checkoutAfter(start, user, book));
            start.countDown();
            int successful = (first.get() ? 1 : 0) + (second.get() ? 1 : 0);
            assertEquals(1, successful);
            assertEquals(0, quantity(book));
            assertEquals(1, orderCount(user));
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void concurrentCancelRestoresOnlyOnce() throws Exception {
        int user = createUser(false);
        int book = createBook(1);
        int order = service.createOrder(user, Map.of(book, 1), "Buyer", "0123456789",
                "buyer@example.com", "Address", null);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> first = pool.submit(() -> { start.await(); return service.cancelOrderByUser(order, user); });
            Future<Boolean> second = pool.submit(() -> { start.await(); return service.cancelOrderByUser(order, user); });
            start.countDown();
            assertEquals(1, (first.get() ? 1 : 0) + (second.get() ? 1 : 0));
            assertEquals(1, quantity(book));
            assertEquals(OrderStatus_24162073.CANCELLED,
                    service.getOrderDetailForUser(order, user).getStatus());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void ownershipAndAdminTransitionsAreEnforced() throws Exception {
        int owner = createUser(false);
        int stranger = createUser(false);
        int admin = createUser(true);
        int book = createBook(1);
        int order = service.createOrder(owner, Map.of(book, 1), "Buyer", "0123456789",
                "buyer@example.com", "Address", null);
        assertNull(service.getOrderDetailForUser(order, stranger));
        assertThrows(SecurityException.class, () -> service.getOrderDetailForAdmin(stranger, order));
        assertTrue(service.updateOrderStatus(admin, order, OrderStatus_24162073.CONFIRMED));
        assertFalse(service.cancelOrderByUser(order, owner));
        assertTrue(service.updateOrderStatus(admin, order, OrderStatus_24162073.SHIPPING));
        assertFalse(service.cancelOrderByAdmin(admin, order));
        assertFalse(service.updateOrderStatus(admin, order, OrderStatus_24162073.PENDING));
        assertTrue(service.updateOrderStatus(admin, order, OrderStatus_24162073.DELIVERED));
    }

    @Test
    void adminCanCancelConfirmedOrderAndRestoreInventory() throws Exception {
        int owner = createUser(false);
        int admin = createUser(true);
        int book = createBook(1);
        int order = service.createOrder(owner, Map.of(book, 1), "Buyer", "0123456789",
                "buyer@example.com", "Address", null);
        assertTrue(service.updateOrderStatus(admin, order, OrderStatus_24162073.CONFIRMED));
        assertTrue(service.cancelOrderByAdmin(admin, order));
        assertFalse(service.cancelOrderByAdmin(admin, order));
        assertEquals(1, quantity(book));
        assertEquals(OrderStatus_24162073.CANCELLED,
                service.getOrderDetailForAdmin(admin, order).getStatus());
    }

    @Test
    void softDeletedBookIsUnavailableButOrderSnapshotSurvives() throws Exception {
        int user = createUser(false);
        int book = createBook(2);
        int order = service.createOrder(user, Map.of(book, 1), "Buyer", "0123456789",
                "buyer@example.com", "Address", null);
        try (Connection conn = connection()) {
            assertEquals(1, new BookDAO_24162073().softDelete(conn, book));
        }
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(user, Map.of(book, 1),
                "Buyer", "0123456789", "buyer@example.com", "Address", null));
        assertEquals("Test Book",
                service.getOrderDetailForUser(order, user).getItems().get(0).getBookTitle());
    }

    @Test
    void failedOrderItemInsertRollsBackOrderAndQuantity() throws Exception {
        int user = createUser(false);
        int book = createBook(2);
        try (Connection conn = connection(); Statement statement = conn.createStatement()) {
            statement.execute("ALTER TABLE order_items ADD CONSTRAINT CK_it_one CHECK (quantity <= 1)");
        }
        try {
            assertThrows(SQLException.class, () -> service.createOrder(user, Map.of(book, 2),
                    "Buyer", "0123456789", "buyer@example.com", "Address", null));
            assertEquals(2, quantity(book));
            assertEquals(0, orderCount(user));
        } finally {
            try (Connection conn = connection(); Statement statement = conn.createStatement()) {
                statement.execute("ALTER TABLE order_items DROP CONSTRAINT CK_it_one");
            }
        }
    }

    private static boolean checkoutAfter(CountDownLatch start, int user, int book) throws Exception {
        start.await();
        try {
            service.createOrder(user, Map.of(book, 1), "Buyer", "0123456789",
                    "buyer@example.com", "Address", null);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static Connection connection() throws SQLException {
        Connection conn = DBConnection_24162073.openConnection();
        conn.setCatalog(database);
        return conn;
    }

    private static int createUser(boolean admin) throws SQLException {
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (email,fullname,phone,passwd,is_admin) VALUES (?,?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, UUID.randomUUID() + "@example.com");
            ps.setString(2, "Test User");
            ps.setString(3, "0123456789");
            ps.setString(4, "test-only");
            ps.setBoolean(5, admin);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                assertTrue(keys.next());
                return keys.getInt(1);
            }
        }
    }

    private static int createBook(int quantity) throws SQLException {
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO books (isbn,title,publisher,price,quantity) VALUES (?,?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, 1001);
            ps.setString(2, "Test Book");
            ps.setString(3, "Test Publisher");
            ps.setBigDecimal(4, new BigDecimal("100.00"));
            ps.setInt(5, quantity);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                assertTrue(keys.next());
                return keys.getInt(1);
            }
        }
    }

    private static int quantity(int bookId) throws SQLException {
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement("SELECT quantity FROM books WHERE bookid=?")) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }

    private static int orderCount(int userId) throws SQLException {
        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM orders WHERE userid=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }
}
