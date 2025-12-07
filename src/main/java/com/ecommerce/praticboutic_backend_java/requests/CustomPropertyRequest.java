package com.ecommerce.praticboutic_backend_java.requests;


/**
 * Classe pour les requêtes de récupération de propriété de customer
 */
public class CustomPropertyRequest {
    private Long bouticid;

    private String prop;

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
}
