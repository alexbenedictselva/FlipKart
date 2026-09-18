package com.example.dto;

public class CartItemsResponse {
    int productInDisplayId;
    String name;

    public CartItemsResponse() {
    }

    public CartItemsResponse(int productInDisplayId, String name) {
        this.productInDisplayId = productInDisplayId;
        this.name = name;
    }

    public int getProductInDisplayId() {
        return productInDisplayId;
    }

    public void setProductInDisplayId(int productInDisplayId) {
        this.productInDisplayId = productInDisplayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
