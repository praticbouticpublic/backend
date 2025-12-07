package com.ecommerce.praticboutic_backend_java.requests;


public class ParamRequest {

    private String param;
    private String valeur;
    private String bouticid;

    // Getters et setters
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public String getBouticid() {
        return bouticid;
    }

    public void setBouticid(String bouticid) {
        this.bouticid = bouticid;
    }
}
