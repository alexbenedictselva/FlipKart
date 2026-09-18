package com.example.model;

public class Product {
    int productId;
    String name;
    String description;

    public Product(int productId, String description, String name) {
        this.productId = productId;
        this.description = description;
        this.name = name;
    }

    public Product() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
