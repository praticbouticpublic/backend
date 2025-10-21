package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

/**
 * Entité Paramètre
 */
@Entity
@Table(name = "PARAMETRE")
public class Parametre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paramid")
    private Integer paramid;

    private Integer customid;
    private String nom;
    private String valeur;
    private String commentaire;

    // Constructeur sans arguments obligatoire pour JPA
    public Parametre() {}


    public Parametre(Integer customid, String nom, String valeur, String commentaire) {
        this.customid = customid;
        this.nom = nom;
        this.valeur = valeur;
        this.commentaire = commentaire;
    }

    public Integer getCustomid() { return customid; }
    public void setCustomid(Integer customid) { this.customid = customid; }

}
