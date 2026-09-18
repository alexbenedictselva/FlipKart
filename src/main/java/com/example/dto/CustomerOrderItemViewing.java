package com.example.dto;

import com.example.model.DeliveryStatus;

public class CustomerOrderItemViewing {
    String productName;
    String vendorName;
    double price;
    int quantity;
    DeliveryStatus status;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public CustomerOrderItemViewing() {
    }


    public CustomerOrderItemViewing(String productName, String vendorName, double price, int quantity, DeliveryStatus status) {
        this.productName = productName;
        this.vendorName = vendorName;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }
}
