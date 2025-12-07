package com.ecommerce.praticboutic_backend_java.requests;

public class VueTableRequest {
    private String table;
    private Integer bouticid;
    private String selcol;
    private Integer selid;
    private Integer limite;
    private Integer offset;

    // Getters et Setters
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Integer getBouticid() {
        return bouticid;
    }

    public void setBouticid(Integer bouticid) {
        this.bouticid = bouticid;
    }

    public String getSelcol() {
        return selcol;
    }

    public void setSelcol(String selcol) {
        this.selcol = selcol;
    }

    public Integer getSelid() {
        return selid;
    }

    public void setSelid(Integer selid) {
        this.selid = selid;
    }

    public Integer getLimite() {
        return limite;
    }

    public void setLimite(Integer limite) {
        this.limite = limite;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

}