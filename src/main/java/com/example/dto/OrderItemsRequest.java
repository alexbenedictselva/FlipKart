package com.example.dto;

public class OrderItemsRequest {

    private int productInDisplayId;
    private int quantity;
    private int vendorId;

    public int getProductInDisplayId() {
        return productInDisplayId;
    }

    public void setProductInDisplayId(int productInDisplayId) {
        this.productInDisplayId = productInDisplayId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public OrderItemsRequest(int productInDisplayId, int quantity,int vendorId) {
        this.productInDisplayId = productInDisplayId;
        this.quantity = quantity;
        this.vendorId = vendorId;
    }

    public OrderItemsRequest() {
    }
}
