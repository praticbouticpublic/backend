package com.ecommerce.praticboutic_backend_java.requests;

public class ColorRowRequest {
    private Long bouticid;
    private Integer limite;
    private Integer offset;

    // Getters et Setters
    public Long getBouticid() {
        return bouticid;
    }

    public void setBouticid(Long bouticid) {
        this.bouticid = bouticid;
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