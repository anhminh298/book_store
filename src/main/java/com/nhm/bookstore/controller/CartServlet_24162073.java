package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.Book_24162073;
import com.nhm.bookstore.model.CartItem_24162073;
import com.nhm.bookstore.model.Cart_24162073;
import com.nhm.bookstore.service.BookService_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet({"/cart", "/cart/add", "/cart/update", "/cart/remove", "/cart/clear"})
public class CartServlet_24162073 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final BookService_24162073 bookService = new BookService_24162073();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        // Any GET request to mutation endpoints redirects to /cart
        if (!"/cart".equals(servletPath)) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        getOrCreateCart(request.getSession());
        request.getRequestDispatcher("/views/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        HttpSession session = request.getSession();
        Cart_24162073 cart = getOrCreateCart(session);

        switch (servletPath) {
            case "/cart/add":
                handleAdd(request, response, session, cart);
                break;
            case "/cart/update":
                handleUpdate(request, response, session, cart);
                break;
            case "/cart/remove":
                handleRemove(request, response, session, cart);
                break;
            case "/cart/clear":
                handleClear(request, response, session, cart);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/cart");
                break;
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response,
                           HttpSession session, Cart_24162073 cart) throws IOException {
        String bookIdParam = request.getParameter("bookId");
        String quantityParam = request.getParameter("quantity");
        String redirectUrl = request.getParameter("redirect");

        int bookId;
        int quantity = 1;

        try {
            bookId = Integer.parseInt(bookIdParam);
            if (quantityParam != null && !quantityParam.trim().isEmpty()) {
                quantity = Math.max(1, Integer.parseInt(quantityParam.trim()));
            }
        } catch (NumberFormatException e) {
            session.setAttribute("cartError", "Thông tin sản phẩm không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        Book_24162073 book = bookService.getById(bookId);
        if (book == null) {
            session.setAttribute("cartError", "Sách không tồn tại.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (!isBookActive(book)) {
            session.setAttribute("cartError", "Sách này hiện không còn kinh doanh.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (book.getQuantity() <= 0) {
            session.setAttribute("cartError", "Sách \"" + book.getTitle() + "\" hiện đã hết hàng.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        CartItem_24162073 currentItem = cart.getItem(bookId);
        int currentQty = currentItem != null ? currentItem.getQuantity() : 0;
        int maxStock = book.getQuantity();

        if (currentQty >= maxStock) {
            session.setAttribute("cartError", "Số lượng trong giỏ đã đạt tối đa tồn kho (" + maxStock + ").");
        } else {
            cart.addItem(book, quantity);
            int newQty = cart.getItem(bookId).getQuantity();
            if (currentQty + quantity > maxStock) {
                session.setAttribute("cartSuccess", "Đã thêm vào giỏ hàng (giới hạn tồn kho: " + maxStock + ").");
            } else {
                session.setAttribute("cartSuccess", "Đã thêm \"" + book.getTitle() + "\" vào giỏ hàng!");
            }
        }

        if (redirectUrl != null && !redirectUrl.trim().isEmpty() && !redirectUrl.contains("\n") && !redirectUrl.contains("\r")) {
            response.sendRedirect(request.getContextPath() + redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response,
                              HttpSession session, Cart_24162073 cart) throws IOException {
        String bookIdParam = request.getParameter("bookId");
        String action = request.getParameter("action");
        String quantityParam = request.getParameter("quantity");

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("cartError", "Mã sách không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        CartItem_24162073 item = cart.getItem(bookId);
        if (item == null) {
            session.setAttribute("cartError", "Sản phẩm không có trong giỏ hàng.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        Book_24162073 freshBook = bookService.getById(bookId);
        if (freshBook != null) {
            item.setBook(freshBook);
        }

        if ("inc".equalsIgnoreCase(action)) {
            boolean increased = cart.increaseQuantity(bookId);
            if (!increased) {
                session.setAttribute("cartError", "Đã đạt số lượng tồn kho tối đa (" + item.getBook().getQuantity() + ").");
            }
        } else if ("dec".equalsIgnoreCase(action)) {
            cart.decreaseQuantity(bookId);
        } else if (quantityParam != null) {
            try {
                int quantity = Integer.parseInt(quantityParam.trim());
                if (quantity <= 0) {
                    cart.removeItem(bookId);
                    session.setAttribute("cartSuccess", "Đã xóa sản phẩm khỏi giỏ hàng.");
                } else {
                    int maxStock = item.getBook().getQuantity();
                    if (quantity > maxStock) {
                        cart.updateQuantity(bookId, maxStock);
                        session.setAttribute("cartError", "Số lượng yêu cầu vượt quá tồn kho. Đã chỉnh về tối đa (" + maxStock + ").");
                    } else {
                        cart.updateQuantity(bookId, quantity);
                    }
                }
            } catch (NumberFormatException e) {
                session.setAttribute("cartError", "Số lượng không hợp lệ.");
            }
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response,
                              HttpSession session, Cart_24162073 cart) throws IOException {
        String bookIdParam = request.getParameter("bookId");
        try {
            int bookId = Integer.parseInt(bookIdParam);
            cart.removeItem(bookId);
            session.setAttribute("cartSuccess", "Đã xóa sản phẩm khỏi giỏ hàng.");
        } catch (NumberFormatException e) {
            session.setAttribute("cartError", "Mã sách không hợp lệ.");
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleClear(HttpServletRequest request, HttpServletResponse response,
                             HttpSession session, Cart_24162073 cart) throws IOException {
        cart.clear();
        session.setAttribute("cartSuccess", "Đã làm trống giỏ hàng.");
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private Cart_24162073 getOrCreateCart(HttpSession session) {
        Cart_24162073 cart = (Cart_24162073) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart_24162073();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private boolean isBookActive(Book_24162073 book) {
        return book != null && book.isActive();
    }
}
