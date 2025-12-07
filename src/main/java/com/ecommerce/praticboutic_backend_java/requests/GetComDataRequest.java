package com.ecommerce.praticboutic_backend_java.requests;

public class GetComDataRequest {
    private Long cmdid;
    private Long bouticid;

    // Getters et Setters
    public Long getCmdid() {
        return cmdid;
    }

    public void setCmdid(Long cmdid) {
        this.cmdid = cmdid;
    }

    public Long getBouticid() {
        return bouticid;
    }

    public void setBouticid(Long bouticid) {
        this.bouticid = bouticid;
    }
}