package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "barlivr")
public class BarLivr extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "barlivrid")
    private Integer barlivrid;

    @Column(name = "customid", nullable = false)
    private Integer customid;

    @Column(name = "valminin", nullable = false)
    private Float valminin;

    @Column(name = "valmaxex", nullable = false)
    private Float valmaxex;

    @Column(name = "surcout", nullable = false, columnDefinition = "float DEFAULT 0")
    private Float surcout = 0.0f;

    @Column(name = "limitebasse", nullable = false, columnDefinition = "int unsigned DEFAULT 1")
    private Integer limiteBasse = 1;

    @Column(name = "limitehaute", nullable = false, columnDefinition = "int unsigned DEFAULT 1")
    private Integer limiteHaute = 1;

    @Column(name = "actif", nullable = false, columnDefinition = "int unsigned DEFAULT 1")
    private Integer actif = 1;

    // Getters et Setters
    public Integer getBarLivrid() {
        return barlivrid;
    }

    public void setBarLivrid(Integer barlivrid) {
        this.barlivrid = barlivrid;
    }

    public Integer getCustomId() {
        return customid;
    }

    public void setCustomId(Integer customid) {
        this.customid = customid;
    }

    public Float getValminin() {
        return valminin;
    }

    public void setValminin(Float valminin) {
        this.valminin = valminin;
    }

    public Float getValmaxex() {
        return valmaxex;
    }

    public void setValmaxex(Float valmaxex) {
        this.valmaxex = valmaxex;
    }

    public Float getSurcout() {
        return surcout;
    }

    public void setSurcout(Float surcout) {
        this.surcout = surcout;
    }

    public Integer getLimiteBasse() {
        return limiteBasse;
    }

    public void setLimiteBasse(Integer limiteBasse) {
        this.limiteBasse = limiteBasse;
    }

    public Integer getLimiteHaute() {
        return limiteHaute;
    }

    public void setLimiteHaute(Integer limiteHaute) {
        this.limiteHaute = limiteHaute;
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
        row.add(getBarLivrid());
        row.add(getValminin());
        row.add(getValmaxex());
        row.add(getSurcout());
        row.add(actif.toString());
        return row;
    }

}