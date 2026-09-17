package com.example.service;

import com.example.dao.OrderDAO;
import com.example.dto.OrderItemsRequest;

import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();

    public boolean createOrder(
            List<OrderItemsRequest> orderItems,
            int custId
    ) throws SQLException {

        if (custId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than 0"
            );
        }

        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one item"
            );
        }

        for (OrderItemsRequest item : orderItems) {

            if (item == null) {
                throw new IllegalArgumentException(
                        "Order item cannot be null"
                );
            }

            if (item.getProductInDisplayId() <= 0) {
                throw new IllegalArgumentException(
                        "Product listing ID must be greater than 0"
                );
            }

            if (item.getVendorId() <= 0) {
                throw new IllegalArgumentException(
                        "Vendor ID must be greater than 0"
                );
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than 0"
                );
            }
        }

        return orderDAO.createCustomerOrder(
                orderItems,
                custId
        );
    }
}