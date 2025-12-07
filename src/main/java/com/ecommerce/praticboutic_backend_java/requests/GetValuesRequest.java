package com.ecommerce.praticboutic_backend_java.requests;

public class GetValuesRequest {
    private String table;
    private Long bouticid;
    private Long idtoup;

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

    public Long getIdtoup() {
        return idtoup;
    }

    public void setIdtoup(Long idtoup) {
        this.idtoup = idtoup;
    }
}