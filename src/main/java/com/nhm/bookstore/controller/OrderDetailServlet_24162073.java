package com.nhm.bookstore.controller;

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
import java.sql.SQLException;

@WebServlet("/order-detail")
public class OrderDetailServlet_24162073 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final OrderService_24162073 orderService = new OrderService_24162073();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User_24162073 user = session == null ? null : (User_24162073) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        int orderId;
        try {
            orderId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        try {
            // IDOR Protection: Always query by both orderId and current user id
            Order_24162073 order = orderService.getOrderDetailForUser(orderId, user.getId());
            if (order == null) {
                // Order not found or does not belong to this user
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Đơn hàng không tồn tại hoặc bạn không có quyền xem đơn hàng này.");
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/views/order-detail.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/order-history");
        }
    }
}
