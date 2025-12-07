package com.ecommerce.praticboutic_backend_java.responses;

public class CreatePaymentResponse {
    private String clientSecret;

    public CreatePaymentResponse() {
    }

    public CreatePaymentResponse(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}