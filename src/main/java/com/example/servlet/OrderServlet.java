package com.example.servlet;

import com.example.dto.OrderItemsRequest;
import com.example.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {

        setJsonResponse(res);

        try {
            Object userId = req.getAttribute("userId");

            if (userId == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "User authentication required"
                );
                return;
            }

            int customerId = (Integer) userId;

            List<OrderItemsRequest> orderItems =
                    objectMapper.readValue(
                            req.getInputStream(),
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(
                                            List.class,
                                            OrderItemsRequest.class
                                    )
                    );

            boolean created =
                    orderService.createOrder(
                            orderItems,
                            customerId
                    );

            if (!created) {
                sendError(
                        res,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to create order"
                );
                return;
            }

            res.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            objectMapper.writeValue(
                    res.getWriter(),
                    Map.of(
                            "message",
                            "Order created successfully"
                    )
            );

        } catch (IllegalArgumentException e) {

            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
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

    private void setJsonResponse(
            HttpServletResponse res
    ) {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
    }

    private void sendError(
            HttpServletResponse res,
            int status,
            String message
    ) throws IOException {

        res.setStatus(status);

        objectMapper.writeValue(
                res.getWriter(),
                Map.of("error", message)
        );
    }
}