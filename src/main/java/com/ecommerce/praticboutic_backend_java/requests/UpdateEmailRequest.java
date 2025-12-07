package com.ecommerce.praticboutic_backend_java.requests;

/**
 * Classe pour la requête de mise à jour d'email
 */
public class UpdateEmailRequest {
    private String email;

    /**
     * Retourne l'adresse email.
     * @return l'adresse email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Définit l'adresse email.
     * @param email la nouvelle adresse email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}