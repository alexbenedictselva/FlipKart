package com.example.model;

public class Order {
    private int orderId;
    private int custId;
    private double totalAmount;
    private String purchasedDate;

    public String getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(String purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public Order(int orderId, double totalAmount, String purchasedDate, int custId) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.purchasedDate = purchasedDate;
        this.custId = custId;
    }

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
