package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROMOTION")
public class Promotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promoid")
    private Integer promoid;

    @Column(name = "customid", nullable = false)
    private Integer customid;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "taux", nullable = false)
    private Double taux;

    @Column(name = "actif", nullable = false, columnDefinition = "TINYINT UNSIGNED DEFAULT 1")
    private Integer actif = 1;

    // --- Getters and Setters ---

    public Integer getPromoid() {
        return promoid;
    }

    public void setPromoid(Integer promoid) {
        this.promoid = promoid;
    }

    public Integer getCustomid() {
        return customid;
    }

    public void setCustomid(Integer customid) {
        this.customid = customid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getTaux() {
        return taux;
    }

    public void setTaux(Double taux) {
        this.taux = taux;
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
        row.add(promoid);
        row.add(code);
        row.add(taux);
        row.add(actif.toString());
        return row;
    }

}