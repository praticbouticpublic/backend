package com.ecommerce.praticboutic_backend_java.requests;

public class SMSRequest {
    private Integer bouticid;
    private String message;
    private String telephone;

    // Getters et Setters
    public Integer getBouticid() {
        return bouticid;
    }

    public void setBouticid(Integer bouticid) {
        this.bouticid = bouticid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}