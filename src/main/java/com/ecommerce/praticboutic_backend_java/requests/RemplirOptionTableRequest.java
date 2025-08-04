package com.ecommerce.praticboutic_backend_java.requests;

public class RemplirOptionTableRequest {
    private String table;
    private Long bouticid;
    private String colonne;

    // Getters et Setters
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Long getBouticid() {
        return bouticid;
    }

    public void setBouticid(Long bouticid) {
        this.bouticid = bouticid;
    }

    public String getColonne() {
        return colonne;
    }

    public void setColonne(String colonne) {
        this.colonne = colonne;
    }

}