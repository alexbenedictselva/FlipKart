package com.example.model;

public class ProductInDisplay {

    private int productInDisplayId;
    private int productId;
    private int vendorId;
    private int quantity;
    private double price;

    public ProductInDisplay() {
    }

    public ProductInDisplay(
            int productInDisplayId,
            int productId,
            int vendorId,
            int quantity,
            double price
    ) {
        this.productInDisplayId = productInDisplayId;
        this.productId = productId;
        this.vendorId = vendorId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductInDisplayId() {
        return productInDisplayId;
    }

    public void setProductInDisplayId(int productInDisplayId) {
        this.productInDisplayId = productInDisplayId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
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
}