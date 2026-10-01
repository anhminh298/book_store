package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.User_24162073;
import com.nhm.bookstore.service.UserService_24162073;
import com.nhm.bookstore.filter.CsrfFilter_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

@WebServlet("/login")
public class LoginServlet_24162073 extends HttpServlet {
    private UserService_24162073 userService;

    @Override
    public void init() {
        userService = new UserService_24162073();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("passwd");
        if (email == null || password == null || email.isBlank() || password.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email và mật khẩu.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        try {
            User_24162073 user = userService.login(email.trim().toLowerCase(Locale.ROOT), password);
            if (user == null) {
                request.setAttribute("error", "Email hoặc mật khẩu không đúng.");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }
            HttpSession session = request.getSession();
            request.changeSessionId();
            session.removeAttribute(CsrfFilter_24162073.SESSION_ATTRIBUTE);
            session.setAttribute("user", user);
            CsrfFilter_24162073.getOrCreateToken(session);
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException e) {
            getServletContext().log("Login database operation failed", e);
            request.setAttribute("error", "Tạm thời không thể đăng nhập. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
