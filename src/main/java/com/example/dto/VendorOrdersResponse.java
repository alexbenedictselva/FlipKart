package com.example.dto;

public class VendorOrdersResponse {
    String CustomerName;
    String productName;
    int quantity;
    double price;
    String orderAt;

    public VendorOrdersResponse(String customerName, int quantity, String productName, double price, String orderAt) {
        CustomerName = customerName;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
        this.orderAt = orderAt;
    }

    public VendorOrdersResponse() {

    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOrderAt() {
        return orderAt;
    }

    public void setOrderAt(String orderAt) {
        this.orderAt = orderAt;
    }
}
