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

@WebServlet("/verify-otp")
public class OTPServlet_24162073 extends HttpServlet {

    private UserService_24162073 userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService_24162073();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/views/otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inputOtp = request.getParameter("otp");
        HttpSession session = request.getSession();
        String storedOtp = (String) session.getAttribute("otp");

        if (inputOtp != null && inputOtp.equals(storedOtp)) {
            String email = (String) session.getAttribute("reg_email");
            String fullname = (String) session.getAttribute("reg_fullname");
            String phoneStr = (String) session.getAttribute("reg_phone");
            String passwd = (String) session.getAttribute("reg_passwd");
            
            User_24162073 user = new User_24162073();
            user.setEmail(email);
            user.setFullname(fullname);
            user.setPhone(phoneStr);
            user.setPasswd(passwd);
            user.setAdmin(false);

            boolean success = userService.register(user);
            if (success) {
                session.removeAttribute("otp");
                session.removeAttribute("reg_email");
                session.removeAttribute("reg_fullname");
                session.removeAttribute("reg_phone");
                session.removeAttribute("reg_passwd");
                response.sendRedirect(request.getContextPath() + "/login?success=1");
            } else {
                request.setAttribute("error", "Lỗi khi lưu vào cơ sở dữ liệu.");
                request.getRequestDispatcher("/views/otp.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Mã OTP không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/views/otp.jsp").forward(request, response);
        }
    }
}
