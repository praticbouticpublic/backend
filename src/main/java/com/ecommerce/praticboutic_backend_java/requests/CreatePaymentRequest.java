package com.ecommerce.praticboutic_backend_java.requests;





import java.util.List;

public class CreatePaymentRequest {
    private String boutic;
    private List<Item> items;
    private String model;
    private Double fraislivr;
    private String codepromo;

    public String getBoutic() {
        return boutic;
    }

    public void setBoutic(String boutic) {
        this.boutic = boutic;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getFraislivr() {
        return fraislivr;
    }

    public void setFraislivr(Double fraislivr) {
        this.fraislivr = fraislivr;
    }

    public String getCodepromo() {
        return codepromo;
    }

    public void setCodepromo(String codepromo) {
        this.codepromo = codepromo;
    }
}