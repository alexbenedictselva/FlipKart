package com.example.servlet;

import com.example.dto.*;
import com.example.model.Customer;
import com.example.model.Product;
import com.example.security.JwtUtil;
import com.example.service.CustomerService;
import com.example.service.OrderService;
import com.example.service.ProductInDisplayService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CustomerServlet extends HttpServlet {

    private final JwtUtil jwtUtil = new JwtUtil();
    private final CustomerService customerService = new CustomerService();
    private final OrderService orderService = new OrderService();
    private final ProductInDisplayService productInDisplayService = new ProductInDisplayService();
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            sendError(res, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            return;
        }

        switch (path) {
            case "/products" -> getProducts(req, res);
            case "/orders" -> getAllOrders(req, res);
            case "/orderHistory" -> getAllDeliveredOrders(req, res);
            case "/getAllProductCategory" -> getAllProductCategory(req, res);
            case "/currentOrders", "/currentOrder" -> getAllCurrentOrders(req, res);
            default -> sendError(res, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    private void getAllProductCategory(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws IOException {
        getAllProducts(res);
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
            sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
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

            String token = jwtUtil.generateToken(
                    customer.getCustId(),
                    customer.getRole()
            );

            LoginResponse response = new LoginResponse(
                    customer.getCustId(),
                    customer.getRole(),
                    token
            );

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), response);

        } catch (IllegalArgumentException e) {
            sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    /**
     * Returns the product catalogue when no product ID is supplied. When a
     * customer selects a product, the same endpoint returns that product's
     * listings from all vendors.
     */
    private void getProducts(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String productIdParameter = req.getParameter("productId");

        if (productIdParameter == null || productIdParameter.isBlank()) {
            getAllProducts(res);
            return;
        }

        displayAllProducts(productIdParameter, res);
    }

    private void getAllProducts(HttpServletResponse res) throws IOException {
        try {
            List<Product> products = productInDisplayService.getAllProducts();

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), products);

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    private void displayAllProducts(
            String productIdParameter,
            HttpServletResponse res
    ) throws IOException {
        try {
            int productId = Integer.parseInt(productIdParameter);

            List<CustomProductsResponse> productInDisplays =
                    productInDisplayService.displayAllProduct(productId);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    res.getWriter(),
                    productInDisplays
            );

        } catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ProductId must be a valid number"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    public void getAllOrders(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws IOException {

        try {
            int custId = getAuthenticatedUserId(req);

            List<CustomerOrderViewingResponse> customerOrderViewingResponses =
                    orderService.getAllCustomerOrder(custId);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    res.getWriter(),
                    customerOrderViewingResponses
            );

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    public void getAllDeliveredOrders(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws IOException {

        try {
            int custId = getAuthenticatedUserId(req);

            List<CustomerOrderViewingResponse> customerOrderViewingResponses =
                    orderService.getAllCompletedOrder(custId);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    res.getWriter(),
                    customerOrderViewingResponses
            );

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    public void getAllCurrentOrders(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws IOException {

        try {
            int custId = getAuthenticatedUserId(req);

            List<CustomerOrderViewingResponse> customerOrderViewingResponses =
                    orderService.getAllCurrentOrders(custId);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(
                    res.getWriter(),
                    customerOrderViewingResponses
            );

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    private int getAuthenticatedUserId(HttpServletRequest req) {
        Object userId = req.getAttribute("userId");

        if (userId == null) {
            throw new IllegalStateException("Authenticated user ID not found");
        }

        return (Integer) userId;
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
