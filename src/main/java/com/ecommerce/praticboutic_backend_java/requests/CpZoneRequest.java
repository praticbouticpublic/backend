package com.ecommerce.praticboutic_backend_java.requests;

public class CpZoneRequest {
    private String sessionId;
    private String customer;
    private String cp;

    // Getters et Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }
}