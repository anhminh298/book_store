package com.nhm.bookstore.controller.admin;

import com.nhm.bookstore.model.OrderStatus_24162073;
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

@WebServlet({"/admin/orders", "/admin/order-detail", "/admin/order/update-status", "/admin/order/cancel"})
public class AdminOrderServlet_24162073 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final OrderService_24162073 orderService = new OrderService_24162073();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User_24162073 user = getAuthenticatedAdmin(request, response);
        if (user == null) return;

        String servletPath = request.getServletPath();
        if ("/admin/orders".equals(servletPath)) {
            handleList(request, response, user);
        } else if ("/admin/order-detail".equals(servletPath)) {
            handleDetail(request, response, user);
        } else {
            // Mutation endpoints accessed via GET redirect to order list
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User_24162073 user = getAuthenticatedAdmin(request, response);
        if (user == null) return;

        String servletPath = request.getServletPath();
        if ("/admin/order/update-status".equals(servletPath)) {
            handleUpdateStatus(request, response, user);
        } else if ("/admin/order/cancel".equals(servletPath)) {
            handleAdminCancel(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response,
                            User_24162073 admin) throws ServletException, IOException {
        String statusParam = request.getParameter("status");
        OrderStatus_24162073 filterStatus = null;

        if (statusParam != null && !statusParam.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusParam)) {
            try {
                filterStatus = OrderStatus_24162073.valueOf(statusParam.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        try {
            List<Order_24162073> orders = orderService.getAllOrders(admin.getId(), filterStatus);
            request.setAttribute("orders", orders);
            request.setAttribute("currentStatus", filterStatus != null ? filterStatus.name() : "ALL");
            request.setAttribute("statuses", OrderStatus_24162073.values());
            request.getRequestDispatcher("/views/admin/order-list.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi truy vấn danh sách đơn hàng.");
            request.getRequestDispatcher("/views/admin/order-list.jsp").forward(request, response);
        }
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response,
                              User_24162073 admin) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        int orderId;
        try {
            orderId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }

        try {
            Order_24162073 order = orderService.getOrderDetailForAdmin(admin.getId(), orderId);
            if (order == null) {
                request.getSession().setAttribute("adminOrderError", "Không tìm thấy đơn hàng #" + orderId);
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/views/admin/order-detail.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response,
                                    User_24162073 admin) throws IOException {
        String orderIdParam = request.getParameter("orderId");
        String nextStatusParam = request.getParameter("nextStatus");
        HttpSession session = request.getSession();

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("adminOrderError", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }

        OrderStatus_24162073 nextStatus;
        try {
            nextStatus = OrderStatus_24162073.valueOf(nextStatusParam);
        } catch (Exception e) {
            session.setAttribute("adminOrderError", "Trạng thái mới không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/order-detail?id=" + orderId);
            return;
        }

        try {
            if (nextStatus == OrderStatus_24162073.CANCELLED) {
                boolean ok = orderService.cancelOrderByAdmin(admin.getId(), orderId);
                if (ok) {
                    session.setAttribute("adminOrderSuccess", "Đã hủy đơn hàng #" + orderId + " và hoàn trả tồn kho thành công.");
                } else {
                    session.setAttribute("adminOrderError", "Không thể hủy đơn hàng #" + orderId + " (chỉ được hủy đơn PENDING hoặc CONFIRMED).");
                }
            } else {
                boolean ok = orderService.updateOrderStatus(admin.getId(), orderId, nextStatus);
                if (ok) {
                    session.setAttribute("adminOrderSuccess", "Cập nhật đơn hàng #" + orderId + " sang trạng thái " + nextStatus + " thành công.");
                } else {
                    session.setAttribute("adminOrderError", "Chuyển đổi trạng thái sang " + nextStatus + " không hợp lệ theo quy trình.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("adminOrderError", "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/order-detail?id=" + orderId);
    }

    private void handleAdminCancel(HttpServletRequest request, HttpServletResponse response,
                                   User_24162073 admin) throws IOException {
        String orderIdParam = request.getParameter("orderId");
        HttpSession session = request.getSession();

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("adminOrderError", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }

        try {
            boolean ok = orderService.cancelOrderByAdmin(admin.getId(), orderId);
            if (ok) {
                session.setAttribute("adminOrderSuccess", "Đã hủy đơn hàng #" + orderId + " và hoàn trả tồn kho thành công.");
            } else {
                session.setAttribute("adminOrderError", "Không thể hủy đơn hàng #" + orderId + " (chỉ được hủy đơn PENDING hoặc CONFIRMED).");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("adminOrderError", "Lỗi khi hủy đơn hàng: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/order-detail?id=" + orderId);
    }

    private User_24162073 getAuthenticatedAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        User_24162073 user = session == null ? null : (User_24162073) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        if (!user.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang quản trị.");
            return null;
        }
        return user;
    }
}
