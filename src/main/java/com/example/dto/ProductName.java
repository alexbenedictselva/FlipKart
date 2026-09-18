package com.example.dto;

public class ProductName {
    int productInDisplayId;
    String productName;

    public int getProductInDisplayId() {
        return productInDisplayId;
    }

    public void setProductInDisplayId(int productInDisplayId) {
        this.productInDisplayId = productInDisplayId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductName() {
    }

    public ProductName(int productInDisplayId, String productName) {
        this.productInDisplayId = productInDisplayId;
        this.productName = productName;
    }
}
