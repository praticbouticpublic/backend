package com.ecommerce.praticboutic_backend_java.requests;

import java.util.ArrayList;
import java.util.List;

public class DepartCommandeRequest {
    private String nom;
    private String prenom;
    private String adresse1;
    private String adresse2;
    private String codepostal;
    private String ville;
    private String telephone;
    private String paiement;
    private String vente;
    private String infosup;
    private List<Item> items;
    private Double remise;
    private Double fraislivr;

    // Getter et Setter pour method
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Getter et Setter pour method
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    // Accesseurs pour l'attribut adr1
    public String getAdresse1() {
        return this.adresse1;
    }

    public void setAdr1(String adresse1) {
        this.adresse1 = adresse1;
    }

    // Accesseurs pour l'attribut adr2
    public String getAdresse2() {
        return this.adresse2;
    }

    public void setAdr2(String adresse2) {
        this.adresse2 = adresse2;
    }

    // Accesseurs pour l'attribut codepostal
    public String getCodePostal() {
        return this.codepostal;
    }

    public void setCodePostal(String codepostal) {
        this.codepostal = codepostal;
    }

    // Accesseurs pour l'attribut ville
    public String getVille() {
        return this.ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    // Accesseurs pour l'attribut infoSup
    public String getInfoSup() {
        return this.infosup;
    }

    public void setInfoSup(String infoSup) {
        this.infosup = infoSup;
    }

    // Accesseurs pour l'attribut remise
    public double getRemise() {
        return this.remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    // Accesseurs pour l'attribut fraislivr
    public double getFraislivr() {
        return this.fraislivr;
    }

    public void setFraislivr(double fraislivr) {
        this.fraislivr = fraislivr;
    }

    // Accesseurs pour l'attribut items
    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    // Getter et Setter pour method
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    // Getter et Setter pour method
    public String getVente() {
        return vente;
    }

    public void setVente(String vente) {
        this.vente = vente;
    }

    // Getter et Setter pour method
    public String getPaiement() {
        return paiement;
    }

    public void setPaiement(String paiement) {
        this.paiement = paiement;
    }

    // Méthodes utilitaires additionnelles pour la liste (optionnelles)
    public void addItem(Item item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    public void removeItem(Item item) {
        if (this.items != null) {
            this.items.remove(item);
        }
    }

    public boolean containsItem(Item item) {
        return this.items != null && this.items.contains(item);
    }

    public int getItemCount() {
        return this.items == null ? 0 : this.items.size();
    }

}

