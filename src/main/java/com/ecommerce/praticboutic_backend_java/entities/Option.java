package com.ecommerce.praticboutic_backend_java.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une option dans l'application
 */
@Entity
@Table(name = "OPTION") // Le mot "option" étant réservé, on ajoute le "_"
public class Option implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "optid")
    private Integer optid;

    @Column(name = "customid", nullable = false)
    private Integer customid;

    @Column(name = "nom", unique = true, nullable = false, length = 150)
    private String nom;

    @Column(name = "surcout", nullable = false)
    private Double surcout;

    @Column(name = "grpoptid", nullable = false)
    private Integer grpoptid;

    @Column(name = "visible", nullable = false)
    private Integer visible = 1;

    // Relation ManyToOne avec GroupeOpt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grpoptid", insertable = false, updatable = false)
    @JsonIgnoreProperties("options")
    private GroupeOpt groupeOption;

    // Constructeurs
    public Option() {}

    public Option(Integer customid, String nom, Double surcout, Integer grpoptid) {
        this.customid = customid;
        this.nom = nom;
        this.surcout = surcout;
        this.grpoptid = grpoptid;
    }

    // Getters / Setters
    public Integer getOptId() {
        return optid;
    }

    public void setOptId(Integer optid) {
        this.optid = optid;
    }

    public Integer getCustomId() {
        return customid;
    }

    public void setCustomId(Integer customid) {
        this.customid = customid;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getSurcout() {
        return surcout;
    }

    public void setSurcout(Double surcout) {
        this.surcout = surcout;
    }

    public Integer getGroupeOptionId() {
        return grpoptid;
    }

    public void setGroupeOptionId(Integer grpoptid) {
        this.grpoptid = grpoptid;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public GroupeOpt getGroupeOption() {
        return groupeOption;
    }

    public void setGroupeOption(GroupeOpt groupeOption) {
        this.groupeOption = groupeOption;
        if (groupeOption != null) {
            this.grpoptid = groupeOption.getGrpoptid(); // synchronise l'ID
        }
    }

    @Transient
    public boolean isVisible() {
        return visible != null && visible == 1;
    }

    // Méthode pour l'affichage en tableau ou liste
    public List<Object> getDisplayData() {
        List<Object> row = new ArrayList<>();
        row.add(getOptId());
        row.add(getNom());
        row.add(getSurcout());
        row.add(groupeOption != null ? groupeOption.getNom() : null);
        row.add(isVisible()); // <- chaîne "1" ou "0"
        return row;
    }


    @Override
    public String toString() {
        return "Option{" +
                "optid=" + optid +
                ", customid=" + customid +
                ", nom='" + nom + '\'' +
                ", surcout=" + surcout +
                ", grpoptid=" + grpoptid +
                ", visible=" + visible +
                '}';
    }
}
