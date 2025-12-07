package com.ecommerce.praticboutic_backend_java.requests;

/**
 * Classe représentant les paramètres de création d'un abonnement
 */
public class SubscriptionRequest {
    private String priceid;
    
    public String getPriceid() {
        return priceid;
    }
    
    public void setPriceid(String priceid) {
        this.priceid = priceid;
    }
}