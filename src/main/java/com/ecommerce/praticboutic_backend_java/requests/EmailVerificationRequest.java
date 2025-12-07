package com.ecommerce.praticboutic_backend_java.requests;

public class EmailVerificationRequest {
    private String sessionId;
    private String email;

    // Getters et Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}