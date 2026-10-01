package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.CartItem_24162073;
import com.nhm.bookstore.model.Cart_24162073;
import com.nhm.bookstore.model.Order_24162073;
import com.nhm.bookstore.model.User_24162073;
import com.nhm.bookstore.service.OrderService_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/checkout", "/checkout/prepare", "/checkout/success"})
public class CheckoutServlet_24162073 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String SESSION_CHECKOUT_SELECTED_IDS = "checkoutSelectedBookIds";

    private final OrderService_24162073 orderService = new OrderService_24162073();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        HttpSession session = request.getSession(false);
        User_24162073 user = session == null ? null : (User_24162073) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("/checkout/success".equals(servletPath)) {
            handleSuccessView(request, response, user);
            return;
        }

        if (!"/checkout".equals(servletPath)) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        handleCheckoutView(request, response, session);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        HttpSession session = request.getSession(false);
        User_24162073 user = session == null ? null : (User_24162073) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("/checkout/prepare".equals(servletPath)) {
            synchronized (session) {
                handlePrepare(request, response, session);
            }
        } else if ("/checkout".equals(servletPath)) {
            synchronized (session) {
                handlePlaceOrder(request, response, session, user);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handlePrepare(HttpServletRequest request, HttpServletResponse response,
                               HttpSession session) throws IOException {
        String[] selectedBookIds = request.getParameterValues("selectedBookIds");
        if (selectedBookIds == null || selectedBookIds.length == 0) {
            session.setAttribute("cartError", "Vui lòng chọn ít nhất 1 sản phẩm để tiến hành thanh toán.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        Cart_24162073 cart = (Cart_24162073) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            session.setAttribute("cartError", "Giỏ hàng của bạn đang trống.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        List<Integer> validSelectedIds = new ArrayList<>();
        for (String idStr : selectedBookIds) {
            try {
                int bookId = Integer.parseInt(idStr.trim());
                if (cart.getItem(bookId) != null && !validSelectedIds.contains(bookId)) {
                    validSelectedIds.add(bookId);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        if (validSelectedIds.isEmpty()) {
            session.setAttribute("cartError", "Các sản phẩm được chọn không tồn tại trong giỏ hàng.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        session.setAttribute(SESSION_CHECKOUT_SELECTED_IDS, validSelectedIds);
        response.sendRedirect(request.getContextPath() + "/checkout");
    }

    @SuppressWarnings("unchecked")
    private void handleCheckoutView(HttpServletRequest request, HttpServletResponse response,
                                    HttpSession session) throws ServletException, IOException {
        List<Integer> selectedIds = (List<Integer>) session.getAttribute(SESSION_CHECKOUT_SELECTED_IDS);
        Cart_24162073 cart = (Cart_24162073) session.getAttribute("cart");

        if (selectedIds == null || selectedIds.isEmpty() || cart == null || cart.isEmpty()) {
            session.setAttribute("cartError", "Vui lòng chọn sản phẩm trong giỏ hàng trước khi đặt hàng.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        List<CartItem_24162073> checkoutItems = new ArrayList<>();
        BigDecimal estimatedTotal = BigDecimal.ZERO;

        for (Integer bookId : selectedIds) {
            CartItem_24162073 item = cart.getItem(bookId);
            if (item != null) {
                checkoutItems.add(item);
                estimatedTotal = estimatedTotal.add(item.getSubtotal());
            }
        }

        if (checkoutItems.isEmpty()) {
            session.removeAttribute(SESSION_CHECKOUT_SELECTED_IDS);
            session.setAttribute("cartError", "Các sản phẩm đã chọn không còn trong giỏ hàng.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        request.setAttribute("checkoutItems", checkoutItems);
        request.setAttribute("estimatedTotal", estimatedTotal);
        request.getRequestDispatcher("/views/checkout.jsp").forward(request, response);
    }

    @SuppressWarnings("unchecked")
    private void handlePlaceOrder(HttpServletRequest request, HttpServletResponse response,
                                  HttpSession session, User_24162073 user) throws ServletException, IOException {
        List<Integer> selectedIds = (List<Integer>) session.getAttribute(SESSION_CHECKOUT_SELECTED_IDS);
        Cart_24162073 cart = (Cart_24162073) session.getAttribute("cart");

        if (selectedIds == null || selectedIds.isEmpty() || cart == null || cart.isEmpty()) {
            session.setAttribute("cartError", "Phiên đặt hàng đã hết hạn hoặc không có sản phẩm được chọn.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        String receiverName = request.getParameter("receiverName");
        String receiverPhone = request.getParameter("receiverPhone");
        String receiverEmail = request.getParameter("receiverEmail");
        String shippingAddress = request.getParameter("shippingAddress");
        String note = request.getParameter("note");

        // Web-layer validation
        String validationError = validateShippingInput(receiverName, receiverPhone, receiverEmail, shippingAddress, note);
        if (validationError != null) {
            prepareCheckoutViewAttributes(request, cart, selectedIds);
            request.setAttribute("error", validationError);
            request.setAttribute("receiverName", receiverName);
            request.setAttribute("receiverPhone", receiverPhone);
            request.setAttribute("receiverEmail", receiverEmail);
            request.setAttribute("shippingAddress", shippingAddress);
            request.setAttribute("note", note);
            request.getRequestDispatcher("/views/checkout.jsp").forward(request, response);
            return;
        }

        Map<Integer, Integer> items = new LinkedHashMap<>();
        for (Integer bookId : selectedIds) {
            CartItem_24162073 item = cart.getItem(bookId);
            if (item != null && item.getQuantity() > 0) {
                items.put(bookId, item.getQuantity());
            }
        }

        if (items.isEmpty()) {
            session.removeAttribute(SESSION_CHECKOUT_SELECTED_IDS);
            session.setAttribute("cartError", "Không có sản phẩm hợp lệ để đặt hàng.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            int orderId = orderService.createOrder(
                    user.getId(),
                    items,
                    receiverName.trim(),
                    receiverPhone.trim(),
                    receiverEmail.trim(),
                    shippingAddress.trim(),
                    note != null && !note.trim().isEmpty() ? note.trim() : null
            );

            // Transaction committed successfully: update session cart & selection
            cart.removePurchasedItems(selectedIds);
            session.removeAttribute(SESSION_CHECKOUT_SELECTED_IDS);

            response.sendRedirect(request.getContextPath() + "/checkout/success?id=" + orderId);

        } catch (IllegalStateException e) {
            // Handled backend error like insufficient inventory or inactive book
            prepareCheckoutViewAttributes(request, cart, selectedIds);
            String msg = e.getMessage();
            if (msg != null && msg.contains("Insufficient quantity")) {
                request.setAttribute("error", "Rất tiếc! Một số sản phẩm trong đơn hàng không còn đủ tồn kho. Vui lòng kiểm tra lại số lượng trong giỏ hàng.");
            } else if (msg != null && msg.contains("inactive")) {
                request.setAttribute("error", "Một số sản phẩm trong đơn hàng hiện đã ngừng kinh doanh.");
            } else {
                request.setAttribute("error", "Không thể đặt hàng lúc này. Vui lòng kiểm tra lại giỏ hàng.");
            }
            request.setAttribute("receiverName", receiverName);
            request.setAttribute("receiverPhone", receiverPhone);
            request.setAttribute("receiverEmail", receiverEmail);
            request.setAttribute("shippingAddress", shippingAddress);
            request.setAttribute("note", note);
            request.getRequestDispatcher("/views/checkout.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            prepareCheckoutViewAttributes(request, cart, selectedIds);
            request.setAttribute("error", "Dữ liệu đặt hàng không hợp lệ. Vui lòng kiểm tra lại thông tin.");
            request.getRequestDispatcher("/views/checkout.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            prepareCheckoutViewAttributes(request, cart, selectedIds);
            request.setAttribute("error", "Hệ thống gặp sự cố khi xử lý đơn hàng. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/views/checkout.jsp").forward(request, response);
        }
    }

    private void handleSuccessView(HttpServletRequest request, HttpServletResponse response,
                                   User_24162073 user) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        int orderId = 0;
        try {
            if (idParam != null) {
                orderId = Integer.parseInt(idParam.trim());
            }
        } catch (NumberFormatException ignored) {
        }

        if (orderId <= 0) {
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        try {
            Order_24162073 order = orderService.getOrderDetailForUser(orderId, user.getId());
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/order-history");
                return;
            }
            request.setAttribute("order", order);
            request.setAttribute("orderId", orderId);
            request.getRequestDispatcher("/views/order-success.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/order-history");
        }
    }

    private void prepareCheckoutViewAttributes(HttpServletRequest request, Cart_24162073 cart,
                                               List<Integer> selectedIds) {
        List<CartItem_24162073> checkoutItems = new ArrayList<>();
        BigDecimal estimatedTotal = BigDecimal.ZERO;
        for (Integer bookId : selectedIds) {
            CartItem_24162073 item = cart.getItem(bookId);
            if (item != null) {
                checkoutItems.add(item);
                estimatedTotal = estimatedTotal.add(item.getSubtotal());
            }
        }
        request.setAttribute("checkoutItems", checkoutItems);
        request.setAttribute("estimatedTotal", estimatedTotal);
    }

    private String validateShippingInput(String name, String phone, String email, String address, String note) {
        if (name == null || name.trim().isEmpty()) {
            return "Họ tên người nhận không được để trống.";
        }
        if (name.trim().length() > 100) {
            return "Họ tên người nhận không được vượt quá 100 ký tự.";
        }
        if (phone == null || phone.trim().isEmpty()) {
            return "Số điện thoại nhận hàng không được để trống.";
        }
        if (phone.trim().length() > 20) {
            return "Số điện thoại không được vượt quá 20 ký tự.";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email nhận hàng không được để trống.";
        }
        if (!email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Địa chỉ email không đúng định dạng.";
        }
        if (email.trim().length() > 255) {
            return "Email không được vượt quá 255 ký tự.";
        }
        if (address == null || address.trim().isEmpty()) {
            return "Địa chỉ nhận hàng không được để trống.";
        }
        if (address.trim().length() > 500) {
            return "Địa chỉ nhận hàng không được vượt quá 500 ký tự.";
        }
        if (note != null && note.trim().length() > 500) {
            return "Ghi chú không được vượt quá 500 ký tự.";
        }
        return null;
    }
}
