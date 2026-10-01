package com.nhm.bookstore.filter;

import com.nhm.bookstore.model.User_24162073;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter_24162073 implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        HttpSession session = req.getSession(false);
        
        if (session != null && session.getAttribute("user") != null) {
            User_24162073 user = (User_24162073) session.getAttribute("user");
            if (user.isAdmin()) {
                chain.doFilter(request, response);
                return;
            }
        }
        
        res.sendRedirect(req.getContextPath() + "/home");
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
