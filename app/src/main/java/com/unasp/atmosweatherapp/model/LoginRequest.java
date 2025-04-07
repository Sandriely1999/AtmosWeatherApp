package com.unasp.atmosweatherapp.model;

public class LoginRequest {
    private String username; // Mudar de email para username
    private String password; // Mudar de senha para password

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}