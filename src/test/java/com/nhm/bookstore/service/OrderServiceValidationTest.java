package com.nhm.bookstore.service;

import com.nhm.bookstore.model.OrderStatus_24162073;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceValidationTest {
    @Test
    void rejectsInvalidCheckoutInput() {
        assertThrows(IllegalArgumentException.class, () ->
                OrderService_24162073.validateRequest(1, Map.of(), "A", "0123456789",
                        "a@example.com", "Address", null));
        assertThrows(IllegalArgumentException.class, () ->
                OrderService_24162073.validateRequest(1, Map.of(1, 0), "A", "0123456789",
                        "a@example.com", "Address", null));
        assertThrows(IllegalArgumentException.class, () ->
                OrderService_24162073.validateRequest(1, Map.of(1, 1), "A", "0123456789",
                        "invalid", "Address", null));
    }

    @Test
    void onlyAllowsForwardNonCancellationTransitions() {
        assertTrue(OrderStatus_24162073.PENDING.canAdvanceTo(OrderStatus_24162073.CONFIRMED));
        assertTrue(OrderStatus_24162073.CONFIRMED.canAdvanceTo(OrderStatus_24162073.SHIPPING));
        assertTrue(OrderStatus_24162073.SHIPPING.canAdvanceTo(OrderStatus_24162073.DELIVERED));
        assertFalse(OrderStatus_24162073.DELIVERED.canAdvanceTo(OrderStatus_24162073.PENDING));
        assertFalse(OrderStatus_24162073.CANCELLED.canAdvanceTo(OrderStatus_24162073.SHIPPING));
        assertFalse(OrderStatus_24162073.PENDING.canAdvanceTo(OrderStatus_24162073.CANCELLED));
    }
}
