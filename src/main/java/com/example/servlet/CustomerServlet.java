package com.example.servlet;

import com.example.dto.CustomerLoginRequest;
import com.example.dto.CustomerRegisterRequest;
import com.example.dto.CustomerResponse;
import com.example.model.Customer;
import com.example.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class CustomerServlet extends HttpServlet {

    private final CustomerService customerService = new CustomerService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            sendError(res, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            return;
        }

        switch (path) {
            case "/register" -> register(req, res);
            case "/login" -> login(req, res);
            default -> sendError(res, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            CustomerRegisterRequest request = objectMapper.readValue(
                    req.getInputStream(),
                    CustomerRegisterRequest.class
            );

            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setAddress(request.getAddress());
            customer.setPhNo(request.getPhNo());
            customer.setPassword(request.getPassword());

            customerService.register(customer);

            res.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(
                    res.getWriter(),
                    new MessageResponse("Customer registered successfully")
            );

        } catch (IllegalArgumentException e) {
            sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            CustomerLoginRequest request = objectMapper.readValue(
                    req.getInputStream(),
                    CustomerLoginRequest.class
            );

            Customer customer = customerService.login(
                    request.getPhNo(),
                    request.getPassword()
            );

            if (customer == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid phone number or password"
                );
                return;
            }

            CustomerResponse response = new CustomerResponse(
                    customer.getCustId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getPhNo()
            );

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), response);

        } catch (IllegalArgumentException e) {
            sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    private void sendError(
            HttpServletResponse res,
            int status,
            String message
    ) throws IOException {
        res.setStatus(status);
        objectMapper.writeValue(
                res.getWriter(),
                new MessageResponse(message)
        );
    }

    public static class MessageResponse {
        private final String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}