package com.example.model;

public class DeliveryPartner {

    private int deliveryPartnerId;
    private String name;
    private String phoneNo;
    private double rating;
    private int numberOfOrdersDelivered;

    public DeliveryPartner() {
    }

    public int getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public void setDeliveryPartnerId(int deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberOfOrdersDelivered() {
        return numberOfOrdersDelivered;
    }

    public void setNumberOfOrdersDelivered(int numberOfOrdersDelivered) {
        this.numberOfOrdersDelivered = numberOfOrdersDelivered;
    }
}