package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.User_24162073;
import com.nhm.bookstore.service.OrderService_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/order/cancel")
public class CancelOrderServlet_24162073 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final OrderService_24162073 orderService = new OrderService_24162073();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Mutation via GET is strictly forbidden
        response.sendRedirect(request.getContextPath() + "/order-history");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User_24162073 user = session == null ? null : (User_24162073) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String orderIdParam = request.getParameter("orderId");
        int orderId;
        try {
            orderId = Integer.parseInt(orderIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("orderError", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        try {
            boolean cancelled = orderService.cancelOrderByUser(orderId, user.getId());
            if (cancelled) {
                session.setAttribute("orderSuccess", "Đã hủy đơn hàng #" + orderId + " thành công và hoàn trả tồn kho.");
            } else {
                session.setAttribute("orderError", "Không thể hủy đơn hàng #" + orderId + ". Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ xác nhận' (PENDING).");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("orderError", "Lỗi hệ thống khi thực hiện hủy đơn hàng. Vui lòng thử lại sau.");
        }

        response.sendRedirect(request.getContextPath() + "/order-detail?id=" + orderId);
    }
}
