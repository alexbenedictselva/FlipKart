package com.example.model;

import java.time.LocalDate;
import java.util.Date;

public class CartItem {
    private int cartItemId;
    private int productInDisplayId;
    private int cartId;
    private LocalDate createdTime;
    private int quantity;

    public CartItem(){
        this.createdTime = LocalDate.now();
    }

    public CartItem(int cartItemId, int productInDisplayId, int cartId, LocalDate createdTime, int quantity) {
        this.cartItemId = cartItemId;
        this.productInDisplayId = productInDisplayId;
        this.cartId = cartId;
        this.createdTime = createdTime;
        this.quantity = quantity;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getProductInDisplayId() {
        return productInDisplayId;
    }

    public void setProductInDisplayId(int productInDisplayId) {
        this.productInDisplayId = productInDisplayId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public LocalDate getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDate createdTime) {
        this.createdTime = createdTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
