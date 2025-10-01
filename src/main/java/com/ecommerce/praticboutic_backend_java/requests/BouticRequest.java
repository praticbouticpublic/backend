package com.ecommerce.praticboutic_backend_java.requests;

public class BouticRequest {

    private String aliasboutic;
    private String nom;
    private String logo;
    private String email;

    // Getters et Setters
    public String getAliasboutic() {
        return aliasboutic;
    }

    public void setAliasboutic(String aliasboutic) {
        this.aliasboutic = aliasboutic;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}