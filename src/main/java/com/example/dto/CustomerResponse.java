package com.example.dto;

public class CustomerResponse {

    private int custId;
    private String name;
    private String address;
    private String phNo;

    public CustomerResponse() {
    }

    public CustomerResponse(
            int custId,
            String name,
            String address,
            String phNo
    ) {
        this.custId = custId;
        this.name = name;
        this.address = address;
        this.phNo = phNo;
    }

    public int getCustId() {
        return custId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhNo() {
        return phNo;
    }
}