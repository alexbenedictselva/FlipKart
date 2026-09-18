package com.example.dto;

public class LoginResponse {

    private int userId;
    private String role;
    private String token;

    public LoginResponse(int userId, String role, String token) {
        this.userId = userId;
        this.role = role;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}