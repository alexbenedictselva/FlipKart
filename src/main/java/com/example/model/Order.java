package com.example.model;

public class Order {
    private int orderId;
    private int custId;
    private double totalAmount;

    public Order(int orderId, int custId, double totalAmount) {
        this.orderId = orderId;
        this.custId = custId;
        this.totalAmount = totalAmount;
    }

    public Order() {
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
