package com.nhm.bookstore.model;

public enum OrderStatus_24162073 {
    PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED;

    public boolean canAdvanceTo(OrderStatus_24162073 next) {
        return (this == PENDING && next == CONFIRMED)
            || (this == CONFIRMED && next == SHIPPING)
            || (this == SHIPPING && next == DELIVERED);
    }
}
