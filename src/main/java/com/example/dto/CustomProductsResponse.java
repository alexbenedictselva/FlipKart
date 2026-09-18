package com.example.dto;

public class CustomProductsResponse {
    int productInDisplayId;
    String productName;
    String vendorName;
    double price;
    int quantity;

    public int getProductInDisplayId() {
        return productInDisplayId;
    }

    public void setProductInDisplayId(int productInDisplayId) {
        this.productInDisplayId = productInDisplayId;
    }

    public CustomProductsResponse(int productInDisplayId, String productName, String vendorName, double price, int quantity) {
        this.productInDisplayId = productInDisplayId;
        this.productName = productName;
        this.vendorName = vendorName;
        this.price = price;
        this.quantity = quantity;
    }

    public CustomProductsResponse(String productName, String vendorName, double price, int quantity) {
        this.productName = productName;
        this.vendorName = vendorName;
        this.price = price;
        this.quantity = quantity;
    }

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

    public CustomProductsResponse() {
    }
}
