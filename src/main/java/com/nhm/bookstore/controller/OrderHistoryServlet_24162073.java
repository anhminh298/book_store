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
import java.util.List;

@WebServlet("/order-history")
public class OrderHistoryServlet_24162073 extends HttpServlet {
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

        try {
            List<Order_24162073> orders = orderService.getOrdersByUser(user.getId());
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/views/order-history.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách đơn hàng lúc này.");
            request.getRequestDispatcher("/views/order-history.jsp").forward(request, response);
        }
    }
}
