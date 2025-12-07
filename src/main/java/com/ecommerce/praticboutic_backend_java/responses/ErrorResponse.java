package com.ecommerce.praticboutic_backend_java.responses;

// Classe pour représenter les réponses d'erreur
public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
