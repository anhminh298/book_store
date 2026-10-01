package com.nhm.bookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem_24162073 implements Serializable {
    private static final long serialVersionUID = 1L;

    private Book_24162073 book;
    private int quantity;

    public CartItem_24162073() {
    }

    public CartItem_24162073(Book_24162073 book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Book_24162073 getBook() {
        return book;
    }

    public void setBook(Book_24162073 book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        if (book == null || book.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return book.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
