package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.User_24162073;
import com.nhm.bookstore.service.UserService_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/verify-otp")
public class OTPServlet_24162073 extends HttpServlet {
    private static final long OTP_TTL_MILLIS = 5 * 60 * 1000L;
    private static final int MAX_ATTEMPTS = 5;
    private UserService_24162073 userService;

    @Override
    public void init() {
        userService = new UserService_24162073();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("otp") == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        request.getRequestDispatcher("/views/otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("otp") == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        Long createdAt = (Long) session.getAttribute("otpCreatedAt");
        Integer attempts = (Integer) session.getAttribute("otpAttempts");
        if (createdAt == null || System.currentTimeMillis() - createdAt > OTP_TTL_MILLIS
                || attempts == null || attempts >= MAX_ATTEMPTS) {
            RegisterServlet_24162073.clearPendingRegistration(session);
            request.setAttribute("error", "Mã OTP đã hết hạn hoặc vượt số lần thử. Vui lòng đăng ký lại.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        String storedOtp = (String) session.getAttribute("otp");
        String inputOtp = request.getParameter("otp");
        if (inputOtp == null || !inputOtp.matches("\\d{6}") || !storedOtp.equals(inputOtp)) {
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                RegisterServlet_24162073.clearPendingRegistration(session);
                request.setAttribute("error", "Bạn đã nhập sai quá số lần cho phép. Vui lòng đăng ký lại.");
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
                return;
            }
            session.setAttribute("otpAttempts", attempts);
            request.setAttribute("error", "Mã OTP không đúng. Còn " + (MAX_ATTEMPTS - attempts) + " lần thử.");
            request.getRequestDispatcher("/views/otp.jsp").forward(request, response);
            return;
        }

        String email = (String) session.getAttribute("reg_email");
        String fullname = (String) session.getAttribute("reg_fullname");
        String phone = (String) session.getAttribute("reg_phone");
        String passwordHash = (String) session.getAttribute("reg_password_hash");
        if (email == null || fullname == null || passwordHash == null) {
            RegisterServlet_24162073.clearPendingRegistration(session);
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        User_24162073 user = new User_24162073();
        user.setEmail(email);
        user.setFullname(fullname);
        user.setPhone(phone);
        user.setAdmin(false);
        try {
            if (userService.register(user, passwordHash)) {
                RegisterServlet_24162073.clearPendingRegistration(session);
                response.sendRedirect(request.getContextPath() + "/login?success=1");
            } else {
                request.setAttribute("error", "Không thể tạo tài khoản. Vui lòng thử lại.");
                request.getRequestDispatcher("/views/otp.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            getServletContext().log("Registration database operation failed", e);
            request.setAttribute("error", "Không thể lưu tài khoản lúc này. Vui lòng thử lại.");
            request.getRequestDispatcher("/views/otp.jsp").forward(request, response);
        }
    }
}
