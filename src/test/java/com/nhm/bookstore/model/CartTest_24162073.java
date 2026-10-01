package com.nhm.bookstore.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CartTest_24162073 {

    private Cart_24162073 cart;
    private Book_24162073 bookA;
    private Book_24162073 bookB;
    private Book_24162073 bookC;

    @BeforeEach
    void setUp() {
        cart = new Cart_24162073();

        bookA = new Book_24162073();
        bookA.setBookid(1);
        bookA.setTitle("Lập trình Java Web");
        bookA.setPrice(new BigDecimal("150000.00"));
        bookA.setQuantity(10);

        bookB = new Book_24162073();
        bookB.setBookid(2);
        bookB.setTitle("Clean Code Tiếng Việt");
        bookB.setPrice(new BigDecimal("200000.00"));
        bookB.setQuantity(5);

        bookC = new Book_24162073();
        bookC.setBookid(3);
        bookC.setTitle("Thiết kế CSDL");
        bookC.setPrice(new BigDecimal("120000.00"));
        bookC.setQuantity(2);
    }

    @Test
    void testAdditionCannotOverflowQuantity() {
        cart.addItem(bookA, 2);
        assertTrue(cart.addItem(bookA, Integer.MAX_VALUE));
        assertEquals(10, cart.getItem(1).getQuantity());
    }

    @Test
    @DisplayName("Thêm mới sản phẩm vào giỏ hàng")
    void testAddNewItem() {
        boolean added = cart.addItem(bookA, 2);
        assertTrue(added);
        assertEquals(1, cart.getItemCount());
        assertEquals(2, cart.getTotalQuantity());
        assertEquals(new BigDecimal("300000.00"), cart.getTotalAmount());
    }

    @Test
    @DisplayName("Thêm sản phẩm đã tồn tại trong giỏ thì tăng số lượng")
    void testAddExistingItemIncreasesQuantity() {
        cart.addItem(bookA, 2);
        boolean addedAgain = cart.addItem(bookA, 3);
        assertTrue(addedAgain);
        assertEquals(5, cart.getItem(1).getQuantity());
        assertEquals(5, cart.getTotalQuantity());
        assertEquals(new BigDecimal("750000.00"), cart.getTotalAmount());
    }

    @Test
    @DisplayName("Thêm số lượng vượt quá tồn kho sẽ bị giới hạn ở mức tối đa tồn kho")
    void testAddExceedingStockClampsToMax() {
        // bookC quantity is 2
        cart.addItem(bookC, 5);
        assertEquals(2, cart.getItem(3).getQuantity());
        assertEquals(new BigDecimal("240000.00"), cart.getTotalAmount());
    }

    @Test
    @DisplayName("Tăng / giảm số lượng trong giỏ hàng")
    void testIncreaseAndDecreaseQuantity() {
        cart.addItem(bookB, 2); // bookB max stock = 5

        assertTrue(cart.increaseQuantity(2));
        assertEquals(3, cart.getItem(2).getQuantity());

        assertTrue(cart.decreaseQuantity(2));
        assertEquals(2, cart.getItem(2).getQuantity());

        // Decrease down to 1
        assertTrue(cart.decreaseQuantity(2));
        assertEquals(1, cart.getItem(2).getQuantity());

        // Decrease when quantity is 1 should not go to 0 or negative
        assertFalse(cart.decreaseQuantity(2));
        assertEquals(1, cart.getItem(2).getQuantity());
    }

    @Test
    @DisplayName("Cập nhật số lượng bằng 0 hoặc âm sẽ xóa sản phẩm")
    void testUpdateQuantityZeroOrNegativeRemovesItem() {
        cart.addItem(bookA, 3);
        cart.updateQuantity(1, 0);
        assertNull(cart.getItem(1));
        assertTrue(cart.isEmpty());

        cart.addItem(bookA, 3);
        cart.updateQuantity(1, -5);
        assertNull(cart.getItem(1));
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("Cập nhật số lượng vượt quá tồn kho sẽ đưa về mức tồn kho tối đa")
    void testUpdateQuantityExceedingStockClamps() {
        cart.addItem(bookB, 1); // max stock 5
        cart.updateQuantity(2, 99);
        assertEquals(5, cart.getItem(2).getQuantity());
    }

    @Test
    @DisplayName("Xóa một sản phẩm khỏi giỏ hàng")
    void testRemoveItem() {
        cart.addItem(bookA, 2);
        cart.addItem(bookB, 1);
        assertEquals(2, cart.getItemCount());

        cart.removeItem(1);
        assertEquals(1, cart.getItemCount());
        assertNull(cart.getItem(1));
        assertNotNull(cart.getItem(2));
    }

    @Test
    @DisplayName("Làm trống toàn bộ giỏ hàng")
    void testClearCart() {
        cart.addItem(bookA, 2);
        cart.addItem(bookB, 1);
        cart.clear();
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getTotalQuantity());
        assertEquals(BigDecimal.ZERO, cart.getTotalAmount());
    }

    @Test
    @DisplayName("Partial checkout: Giỏ hàng A, B, C; mua A và C; giỏ hàng chỉ còn lại B")
    void testPartialCheckoutRemoval() {
        cart.addItem(bookA, 2);
        cart.addItem(bookB, 1);
        cart.addItem(bookC, 2);

        assertEquals(3, cart.getItemCount());
        assertEquals(5, cart.getTotalQuantity());

        // Simulate checkout of selected IDs [1, 3] (A and C)
        cart.removePurchasedItems(Arrays.asList(1, 3));

        // Only B remains
        assertEquals(1, cart.getItemCount());
        assertNull(cart.getItem(1));
        assertNotNull(cart.getItem(2));
        assertNull(cart.getItem(3));

        assertEquals(1, cart.getItem(2).getQuantity());
        assertEquals(new BigDecimal("200000.00"), cart.getTotalAmount());
    }

    @Test
    @DisplayName("Không thêm được sách có quantity <= 0 hoặc null")
    void testInvalidBookOrStock() {
        Book_24162073 outOfStock = new Book_24162073();
        outOfStock.setBookid(99);
        outOfStock.setQuantity(0);

        assertFalse(cart.addItem(outOfStock, 1));
        assertFalse(cart.addItem(null, 1));
        assertFalse(cart.addItem(bookA, 0));
        assertFalse(cart.addItem(bookA, -2));
    }
}
