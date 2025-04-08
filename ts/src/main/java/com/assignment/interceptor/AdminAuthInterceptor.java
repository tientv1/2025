package com.assignment.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");

        // Kiểm tra nếu không phải admin thì redirect về trang chủ
        if (!"ADMIN".equals(role)) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}