package com.example.dto;

public class ProductInDisplayUpdateRequest {

    private Double price;
    private Integer quantity;

    public ProductInDisplayUpdateRequest() {
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}