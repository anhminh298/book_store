package com.nhm.bookstore.controller;

import com.nhm.bookstore.service.EmailService_24162073;
import com.nhm.bookstore.service.UserService_24162073;
import com.nhm.bookstore.util.OTPUtil_24162073;
import com.nhm.bookstore.util.PasswordUtil_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

@WebServlet("/register")
public class RegisterServlet_24162073 extends HttpServlet {
    private UserService_24162073 userService;

    @Override
    public void init() {
        userService = new UserService_24162073();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = value(request, "email").trim().toLowerCase(Locale.ROOT);
        String fullname = value(request, "fullname").trim();
        String phone = value(request, "phone").trim();
        String password = value(request, "passwd");
        String confirm = value(request, "confirmPasswd");

        if (email.isBlank() || email.length() > 255 || fullname.isBlank() || fullname.length() > 100
                || phone.length() > 20 || password.length() < 8 || password.length() > 128) {
            showError(request, response, "Vui lòng kiểm tra email, họ tên, số điện thoại và mật khẩu (8–128 ký tự).");
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showError(request, response, "Địa chỉ email không hợp lệ.");
            return;
        }
        if (!password.equals(confirm)) {
            showError(request, response, "Mật khẩu xác nhận không khớp.");
            return;
        }

        try {
            if (userService.emailExists(email)) {
                showError(request, response, "Email đã tồn tại.");
                return;
            }
        } catch (SQLException e) {
            getServletContext().log("Registration email check failed", e);
            showError(request, response, "Tạm thời không thể đăng ký. Vui lòng thử lại sau.");
            return;
        }

        String otp = OTPUtil_24162073.generateOTP();
        HttpSession session = request.getSession();
        clearPendingRegistration(session);
        session.setAttribute("otp", otp);
        session.setAttribute("otpCreatedAt", System.currentTimeMillis());
        session.setAttribute("otpAttempts", 0);
        session.setAttribute("reg_email", email);
        session.setAttribute("reg_fullname", fullname);
        session.setAttribute("reg_phone", phone.isBlank() ? null : phone);
        session.setAttribute("reg_password_hash", PasswordUtil_24162073.hash(password));

        if (EmailService_24162073.sendOTP(email, otp)) {
            response.sendRedirect(request.getContextPath() + "/verify-otp");
        } else {
            clearPendingRegistration(session);
            showError(request, response, "Không gửi được email OTP. Kiểm tra cấu hình SMTP rồi thử lại.");
        }
    }

    static void clearPendingRegistration(HttpSession session) {
        session.removeAttribute("otp");
        session.removeAttribute("otpCreatedAt");
        session.removeAttribute("otpAttempts");
        session.removeAttribute("reg_email");
        session.removeAttribute("reg_fullname");
        session.removeAttribute("reg_phone");
        session.removeAttribute("reg_password_hash");
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value;
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }
}
