package com.ecommerce.praticboutic_backend_java.requests;

public class SuppressionRequest {
    private String email;
    private Integer bouticid;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getBouticid() {
        return bouticid;
    }

    public void setBouticid(Integer bouticid) {
        this.bouticid = bouticid;
    }

}

