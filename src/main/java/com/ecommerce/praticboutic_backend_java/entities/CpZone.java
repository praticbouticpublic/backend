package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cpzone")
public class CpZone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cpzoneid")
    private Integer cpzoneid;

    @Column(name = "customid", nullable = false)
    private Integer customid;

    @Column(name = "codepostal", nullable = false, length = 5)
    private String codepostal;

    @Column(name = "ville", nullable = false, length = 45)
    private String ville;

    @Column(name = "actif", nullable = false, columnDefinition = "int DEFAULT 1")
    private Integer actif = 1;

    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getCustomid() {
        return customid;
    }

    public void setCustomid(Integer customid) {
        this.customid = customid;
    }

    public String getCodepostal() {
        return codepostal;
    }

    public void setCodepostal(String codepostal) {
        this.codepostal = codepostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Integer getActif() {
        return actif;
    }

    public void setActif(Integer actif) {
        this.actif = actif;
    }

    public List<Object> getDisplayData()
    {
        List<Object> row = new ArrayList<>();
        row.add(cpzoneid);
        row.add(codepostal);
        row.add(ville);
        row.add(actif.toString());
        return row;
    }
}