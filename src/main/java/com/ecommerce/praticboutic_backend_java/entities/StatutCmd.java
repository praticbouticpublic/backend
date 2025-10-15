package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

/**
 * Entité StatutCmd
 */
@Entity
@Table(name = "STATUTCMD")
public class StatutCmd extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statid")
    private Integer statid;

    private Integer customid;
    private String etat;
    private String couleur;
    private String message;
    private Integer defaut;
    private Integer actif;

    // Relation inverse avec Statutcmd - si vous souhaitez la maintenir
    @OneToMany(mappedBy = "statid", fetch = FetchType.LAZY)
    private List<Commande> commandes;

    public StatutCmd() {}

    public StatutCmd( Integer customid, String etat, String couleur, String message, Integer defaut, Integer actif) {
        this.customid = customid;
        this.etat = etat;
        this.couleur = couleur;
        this.message = message;
        this.defaut = defaut;
        this.actif = actif;
    }

    public Integer getStatid() {
        return statid;
    }

    public void setStatid(Integer statid) {
        this.statid = statid;
    }

    public Integer getCustomId() {
        return customid;
    }

    public void setCustomId(Integer customid) {
        this.customid = customid;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDefaut(Integer defaut) {
        this.defaut = defaut;
    }

    public void setActif(Integer actif) {
        this.actif = actif;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setComandes(List<Commande> commandes) {
        this.commandes = commandes;
    }

}