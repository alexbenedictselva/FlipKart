package com.example.dto;

public class CartItemsResponse {
    int productInDisplayId;
    int cartItemId;
    String name;

    public CartItemsResponse(int productInDisplayId, int cartItemId, String name) {
        this.productInDisplayId = productInDisplayId;
        this.cartItemId = cartItemId;
        this.name = name;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

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
