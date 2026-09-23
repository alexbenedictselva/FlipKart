package com.example.dto;

import com.example.model.Variant;

public class CustomProductsResponse {
    int productInDisplayId;
    int vendorId;
    String productName;
    String vendorName;
    double price;
    int quantity;
    Variant variant;

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public int getProductInDisplayId() {
        return productInDisplayId;
    }

    public void setProductInDisplayId(int productInDisplayId) {
        this.productInDisplayId = productInDisplayId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
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
