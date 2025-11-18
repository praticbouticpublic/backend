package com.ecommerce.praticboutic_backend_java.requests;


/**
 * Classe pour les requêtes de mise à jour de propriété de client
 */
public class ClientPropertyUpdateRequest {
    private Long bouticid;

    private String prop;

    private String valeur;

    // Getters et Setters
    public Long getBouticid() {
        return bouticid;
    }

    public void setBouticid(Long bouticid) {
        this.bouticid = bouticid;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}
