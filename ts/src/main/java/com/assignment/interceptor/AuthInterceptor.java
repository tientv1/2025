package com.assignment.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response,
            @SuppressWarnings("null") Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        // Chỉ kiểm tra authentication cho các request tới /order/**
        if (requestURI.startsWith("/order")) {
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("username");

            if (username == null) {
                String requestUrl = request.getRequestURL().toString();
                String queryString = request.getQueryString();
                if (queryString != null) {
                    requestUrl += "?" + queryString;
                }
                session.setAttribute("redirectUrl", requestUrl);

                response.sendRedirect("/auth/login");
                return false;
            }
        }

        return true;
    }
}