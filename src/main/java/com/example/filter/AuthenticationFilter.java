package com.example.filter;

import com.example.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (path.endsWith("/user/login")
                || path.endsWith("/user/register")) {

            chain.doFilter(request, response);
            return;
        }

        String authorization = req.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            sendError(
                    res,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization token required"
            );
            return;
        }

        String token = authorization.substring(7);

        Claims claims = jwtUtil.validateToken(token);

        if (claims == null) {
            sendError(
                    res,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token"
            );
            return;
        }

        int userId = jwtUtil.getUserId(claims);
        String role = jwtUtil.getRole(claims);

        req.setAttribute("userId", userId);
        req.setAttribute("role", role);

        chain.doFilter(request, response);
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