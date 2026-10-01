package com.nhm.bookstore.controller;

import com.nhm.bookstore.service.UserService_24162073;
import com.nhm.bookstore.service.EmailService_24162073;
import com.nhm.bookstore.util.OTPUtil_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet_24162073 extends HttpServlet {

    private UserService_24162073 userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService_24162073();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String fullname = request.getParameter("fullname");
        String phoneStr = request.getParameter("phone");
        String passwd = request.getParameter("passwd");
        String confirmPasswd = request.getParameter("confirmPasswd");

        if (email == null || email.trim().isEmpty() || passwd == null || passwd.trim().isEmpty()) {
            request.setAttribute("error", "Email và mật khẩu không được để trống.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        if (!passwd.equals(confirmPasswd)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        if (userService.emailExists(email)) {
            request.setAttribute("error", "Email đã tồn tại.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        String otp = OTPUtil_24162073.generateOTP();
        
        HttpSession session = request.getSession();
        session.setAttribute("otp", otp);
        session.setAttribute("reg_email", email);
        session.setAttribute("reg_fullname", fullname);
        session.setAttribute("reg_phone", phoneStr);
        session.setAttribute("reg_passwd", passwd);

        boolean emailSent = EmailService_24162073.sendOTP(email, otp);
        
        if (emailSent) {
            response.sendRedirect(request.getContextPath() + "/verify-otp");
        } else {
            request.setAttribute("error", "Không thể gửi email OTP.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        }
    }
}
