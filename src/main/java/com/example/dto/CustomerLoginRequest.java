package com.example.dto;

public class CustomerLoginRequest {

    private String phNo;
    private String password;
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public CustomerLoginRequest(String phNo, String password, String role) {
        this.phNo = phNo;
        this.password = password;
        this.role = role;
    }

    public CustomerLoginRequest() {
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