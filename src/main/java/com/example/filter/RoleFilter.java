package com.example.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RoleFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        String role = (String) req.getAttribute("role");

        if (isPublicPath(req)) {
            chain.doFilter(request, response);
            return;
        }

        if (path.contains("/vendor/products")) {
            if (!"VENDOR".equals(role)) {
                sendError(res, HttpServletResponse.SC_FORBIDDEN, "Vendor access required");
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        if (path.contains("/deliveryPartner")) {
            if (!"DELIVERY_PERSON".equals(role)) {
                sendError(res, HttpServletResponse.SC_FORBIDDEN, "Delivery partner access required");
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        if (path.contains("/user/")) {
            if (!"CUSTOMER".equals(role)) {
                sendError(res, HttpServletResponse.SC_FORBIDDEN, "Customer access required");
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(HttpServletRequest req) {
        String path = req.getRequestURI();
        String contextPath = req.getContextPath();
        String resourcePath = path.startsWith(contextPath)
                ? path.substring(contextPath.length())
                : path;

        return resourcePath.equals("/")
                || resourcePath.equals("/index.html")
                || resourcePath.equals("/customer.html")
                || resourcePath.startsWith("/assets/")
                || resourcePath.equals("/user/login")
                || resourcePath.equals("/user/register");
    }

    private void sendError(
            HttpServletResponse res,
            int status,
            String message
    ) throws IOException {

        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        res.getWriter().write(
                "{\"error\":\"" + message + "\"}"
        );
    }
}
