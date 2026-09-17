package com.example.service;

import com.example.dao.OrderDAO;
import com.example.dto.CustomerOrderItemViewing;
import com.example.dto.CustomerOrderViewingResponse;
import com.example.dto.OrderItemsRequest;
import com.example.model.DeliveryStatus;

import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();

    public List<CustomerOrderViewingResponse> getAllCustomerOrder(int custId) throws SQLException{
        return orderDAO.getAllCustomerOrders(custId);
    }

    public List<CustomerOrderViewingResponse> getAllCurrentOrders(int custId) throws SQLException {
        List<CustomerOrderViewingResponse> customerOrderViewingResponses = getAllCustomerOrder(custId);

        for (int i = customerOrderViewingResponses.size() - 1; i >= 0; i--) {

            List<CustomerOrderItemViewing> items =
                    customerOrderViewingResponses.get(i).getCustomerOrderItemViewings();

            for (int j = items.size() - 1; j >= 0; j--) {
                if (items.get(j).getStatus() == DeliveryStatus.DELIVERED) {
                    items.remove(j);
                }
            }

            if (items.isEmpty()) {
                customerOrderViewingResponses.remove(i);
            }
        }

        return customerOrderViewingResponses;
    }
    public List<CustomerOrderViewingResponse> getAllCompletedOrder(int custId) throws SQLException {
        List<CustomerOrderViewingResponse> customerOrderViewingResponses = getAllCustomerOrder(custId);

        for (int i = customerOrderViewingResponses.size() - 1; i >= 0; i--) {

            List<CustomerOrderItemViewing> items =
                    customerOrderViewingResponses.get(i).getCustomerOrderItemViewings();

            for (int j = items.size() - 1; j >= 0; j--) {
                if (items.get(j).getStatus() != DeliveryStatus.DELIVERED) {
                    items.remove(j);
                }
            }

            if (items.isEmpty()) {
                customerOrderViewingResponses.remove(i);
            }
        }

        return customerOrderViewingResponses;
    }
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