package com.ecommerce.praticboutic_backend_java.entities;

public class Item {
    private String id;
    private String type;
    private String name;
    private Double prix;
    private Integer qt;
    private String unite;
    private String opts;
    private String txta;
    // getters et setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Integer getQt() {
        return qt;
    }

    public void setQt(Integer qt) {
        this.qt = qt;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getOpts() {
        return opts;
    }

    public void setOpts(String opts) {
        this.opts = opts;
    }

    public String getTxta() {
        return txta;
    }

    public void setTxta(String txta) {
        this.txta = txta;
    }
}
