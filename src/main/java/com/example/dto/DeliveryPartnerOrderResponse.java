package com.example.dto;

import java.util.List;

public class DeliveryPartnerOrderResponse {
    int deliveryId;
    List<ProductName> productNames;
    String name;
    String phNo;
    String status;

    public DeliveryPartnerOrderResponse() {
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public List<ProductName> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<ProductName> productNames) {
        this.productNames = productNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DeliveryPartnerOrderResponse(int deliveryId, String phNo, String status, List<ProductName> productNames, String name) {
        this.deliveryId = deliveryId;
        this.phNo = phNo;
        this.status = status;
        this.productNames = productNames;
        this.name = name;
    }
}
