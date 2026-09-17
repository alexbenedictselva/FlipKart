package com.example.model;

public class OrderItems {
    private int orderItemId;
    private int productDisplayId;
    private int orderId;
    private int deliveryId;
    private double price;
    private int quantity;

    public OrderItems(int orderItemId, int productDisplayId, int orderId, int deliveryId, double price, int quantity) {
        this.orderItemId = orderItemId;
        this.productDisplayId = productDisplayId;
        this.orderId = orderId;
        this.deliveryId = deliveryId;
        this.price = price;
        this.quantity = quantity;
    }

    public OrderItems() {
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getProductDisplayId() {
        return productDisplayId;
    }

    public void setProductDisplayId(int productDisplayId) {
        this.productDisplayId = productDisplayId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
