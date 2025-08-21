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
@Table(name = "\"option\"") // Le mot "option" étant un mot réservé en SQL, on utilise des backticks
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
    private Double surcout = 0.0;

    @Column(name = "grpoptid", nullable = false)
    private Integer grpoptid;

    @Column(name = "visible", nullable = false)
    private Integer visible = 1;

    // Relation avec Categorie (si vous souhaitez conserver la relation JPA)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grpoptid", insertable = false, updatable = false) // Utilisez insertable=false, updatable=false pour éviter les conflits
    @JsonIgnoreProperties("options")
    private GroupeOpt groupeopt;

    // Constructeurs
    public Option() {
    }

    public Option(Integer customid, String nom) {
        this.customid = customid;
        this.nom = nom;
    }

    public Option(Integer customid, String nom, Double surcout, Integer groupeOptionId) {
        this.customid = customid;
        this.nom = nom;
        this.surcout = surcout;
    }

    // Getters et setters
    public Integer getOptId() {
        return optid;
    }

    public void setOptId(Integer id) {
        this.optid = id;
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

    public void setSurcout() {
        this.surcout = surcout;
    }

    public Integer getGroupeOptionId() {
        return grpoptid;
    }

    public void setGroupeOptionId(Boolean groupeOptionId) {
        this.grpoptid = grpoptid;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public GroupeOpt getGroupeOption() {
        return groupeopt;
    }

    public void setGroupeOption(GroupeOpt groupeopt) {
        this.groupeopt= groupeopt;
    }

    /**
     * Vérifie si l'option est visible
     * @return true si l'option est visible, false sinon
     */
    @Transient
    public Integer isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return "Option{" +
                "opid=" + optid +
                ", customid=" + customid +
                ", nom='" + nom + '\'' +
                ", surcout=" + surcout +
                ", groupeOptionId=" + grpoptid +
                ", visible=" + visible +
                '}';
    }

    public List<Object> getDisplayData()
    {
        List<Object> row = new ArrayList<>();
        row.add(getOptId());
        row.add(getNom());
        row.add(getSurcout());
        row.add(getGroupeOption().getNom());
        row.add((getVisible()==1) ? "1" : "0");
        return row;
    }


}