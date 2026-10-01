package com.nhm.bookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cart_24162073 implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, CartItem_24162073> items = new LinkedHashMap<>();

    public Cart_24162073() {
    }

    public synchronized boolean addItem(Book_24162073 book, int quantity) {
        if (book == null || quantity <= 0) {
            return false;
        }

        int maxStock = book.getQuantity();
        if (maxStock <= 0) {
            return false;
        }

        int bookId = book.getBookid();
        CartItem_24162073 item = items.get(bookId);

        if (item == null) {
            int toAdd = Math.min(quantity, maxStock);
            items.put(bookId, new CartItem_24162073(book, toAdd));
            return true;
        } else {
            // Update book reference to keep latest info
            item.setBook(book);
            int currentQty = item.getQuantity();
            int newQty = (int) Math.min((long) currentQty + quantity, maxStock);
            item.setQuantity(newQty);
            return newQty > currentQty;
        }
    }

    public synchronized boolean increaseQuantity(int bookId) {
        CartItem_24162073 item = items.get(bookId);
        if (item == null) {
            return false;
        }

        int maxStock = item.getBook().getQuantity();
        if (item.getQuantity() < maxStock) {
            item.setQuantity(item.getQuantity() + 1);
            return true;
        }
        return false;
    }

    public synchronized boolean decreaseQuantity(int bookId) {
        CartItem_24162073 item = items.get(bookId);
        if (item == null) {
            return false;
        }

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            return true;
        }
        return false;
    }

    public synchronized void updateQuantity(int bookId, int quantity) {
        if (quantity <= 0) {
            removeItem(bookId);
            return;
        }

        CartItem_24162073 item = items.get(bookId);
        if (item != null) {
            int maxStock = item.getBook().getQuantity();
            if (maxStock > 0) {
                item.setQuantity(Math.min(quantity, maxStock));
            }
        }
    }

    public synchronized void removeItem(int bookId) {
        items.remove(bookId);
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized CartItem_24162073 getItem(int bookId) {
        return items.get(bookId);
    }

    public synchronized List<CartItem_24162073> getItems() {
        return new ArrayList<>(items.values());
    }

    public synchronized int getTotalQuantity() {
        int total = 0;
        for (CartItem_24162073 item : items.values()) {
            total = (int) Math.min((long) total + item.getQuantity(), Integer.MAX_VALUE);
        }
        return total;
    }

    public synchronized BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem_24162073 item : items.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public synchronized void removePurchasedItems(Collection<Integer> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return;
        }
        for (Integer bookId : bookIds) {
            if (bookId != null) {
                items.remove(bookId);
            }
        }
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    public synchronized int getItemCount() {
        return items.size();
    }
}
