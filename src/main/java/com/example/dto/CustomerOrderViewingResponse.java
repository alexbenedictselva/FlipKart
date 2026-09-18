package com.example.dto;

import java.util.List;

public class CustomerOrderViewingResponse {
    int orderId;
    List<CustomerOrderItemViewing> customerOrderItemViewings;
    String purchasedAt;

    public CustomerOrderViewingResponse(int orderId, List<CustomerOrderItemViewing> customerOrderItemViewings, String purchasedAt) {
        this.orderId = orderId;
        this.customerOrderItemViewings = customerOrderItemViewings;
        this.purchasedAt = purchasedAt;
    }

    public CustomerOrderViewingResponse() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<CustomerOrderItemViewing> getCustomerOrderItemViewings() {
        return customerOrderItemViewings;
    }

    public void setCustomerOrderItemViewings(List<CustomerOrderItemViewing> customerOrderItemViewings) {
        this.customerOrderItemViewings = customerOrderItemViewings;
    }

    public String getPurchasedAt() {
        return purchasedAt;
    }

    public void setPurchasedAt(String purchasedAt) {
        this.purchasedAt = purchasedAt;
    }
}
