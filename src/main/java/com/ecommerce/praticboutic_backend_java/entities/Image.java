package com.ecommerce.praticboutic_backend_java.entities;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité représentant une image d'article dans l'application
 */
@Entity
@Table(name = "artlistimg")
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artlistimgid")
    private Integer id;

    @Column(name = "customid", nullable = false)
    private Integer customid = 0;

    @Column(name = "artid") // ou le nom approprié dans la base de données
    private Integer artid;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "favori", nullable = false)
    private Integer favori = 0;

    @Column(name = "visible", nullable = false)
    private Integer visible = 1;

    // Constructeurs
    public Image() {
    }

    public Image(String image) {
        this.image = image;
    }

    public Image(Integer artid, String image) {
        this.artid = artid;
        this.image = image;
    }

    public Image(Integer customid, Integer artid, String image) {
        this.customid = customid;
        this.artid = artid;
        this.image = image;
    }

    // Getters et setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomId() {
        return customid;
    }

    public void setCustomId(Integer customid) {
        this.customid = customid;
    }

    public Integer getArtid() {
        return artid;
    }

    public void setArtid(Integer artid) {
        this.artid = artid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getFavori() {
        return favori;
    }

    public void setFavori(Integer favori) {
        this.favori = favori;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    /**
     * Vérifie si l'image est marquée comme favorie
     * @return true si l'image est favorie, false sinon
     */
    @Transient
    public boolean isFavori() {
        return favori == 1;
    }

    /**
     * Définit si l'image est favorie
     * @param favori true pour marquer l'image comme favorie, false sinon
     */
    public void setFavori(boolean favori) {
        this.favori = favori ? 1 : 0;
    }

    /**
     * Vérifie si l'image est visible
     * @return true si l'image est visible, false sinon
     */
    @Transient
    public boolean isVisible() {
        return visible == 1;
    }

    /**
     * Définit la visibilité de l'image
     * @param visible true pour rendre l'image visible, false pour la masquer
     */
    public void setVisibility(boolean visible) {
        this.visible = visible ? 1 : 0;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", customid=" + customid +
                ", artid=" + artid +
                ", image='" + image + '\'' +
                ", favori=" + favori +
                ", visible=" + visible +
                '}';
    }
}