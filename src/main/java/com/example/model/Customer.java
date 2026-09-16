package com.example.model;

public class Customer {

    private int custId;
    private String name;
    private String address;
    private String phNo;
    private String password;

    public Customer() {
    }

    public Customer(
            int custId,
            String name,
            String address,
            String phNo,
            String password
    ) {
        this.custId = custId;
        this.name = name;
        this.address = address;
        this.phNo = phNo;
        this.password = password;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}