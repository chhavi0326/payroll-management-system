package com.chhavi.payroll.dto;

public class LoginResponse {

    private String username;
    private String role;
    private String token;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(
            String username,
            String role,
            String token,
            String message) {

        this.username = username;
        this.role = role;
        this.token = token;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}