package com.nhm.bookstore.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CsrfFilter_24162073 implements Filter {
    public static final String SESSION_ATTRIBUTE = "csrfToken";
    public static final String REQUEST_PARAMETER = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String method = req.getMethod();
        if ("POST".equalsIgnoreCase(method)) {
            HttpSession session = req.getSession(false);
            String expected = session == null ? null : (String) session.getAttribute(SESSION_ATTRIBUTE);
            String supplied = req.getParameter(REQUEST_PARAMETER);
            if (supplied == null && req.getContentType() != null
                    && req.getContentType().toLowerCase().startsWith("multipart/form-data")) {
                try {
                    Part tokenPart = req.getPart(REQUEST_PARAMETER);
                    if (tokenPart != null && tokenPart.getSize() <= 128) {
                        supplied = new String(tokenPart.getInputStream().readAllBytes(),
                                StandardCharsets.UTF_8);
                    }
                } catch (IllegalStateException | ServletException | IOException e) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
                    return;
                }
            }
            if (!matches(expected, supplied)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
                return;
            }
        } else if ("GET".equalsIgnoreCase(method) && !isStaticPath(req)) {
            getOrCreateToken(req.getSession());
        }
        chain.doFilter(request, response);
    }

    public static String getOrCreateToken(HttpSession session) {
        synchronized (session) {
            String token = (String) session.getAttribute(SESSION_ATTRIBUTE);
            if (token == null) {
                byte[] bytes = new byte[32];
                RANDOM.nextBytes(bytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(SESSION_ATTRIBUTE, token);
            }
            return token;
        }
    }

    static boolean matches(String expected, String supplied) {
        if (expected == null || supplied == null) return false;
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                                     supplied.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isStaticPath(HttpServletRequest req) {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        return path.startsWith("/css/") || path.startsWith("/uploads/")
                || path.startsWith("/js/") || path.startsWith("/images/");
    }
}
