package com.ecommerce.praticboutic_backend_java.responses;

/**
 * DTO pour les réponses API standardisées
 */

public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }
}
