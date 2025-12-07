package com.ecommerce.praticboutic_backend_java.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ARTLISTIMG")
public class ArtListImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artlistimgid")
    private Integer artlistimgid;

    @Column(name = "customid")
    private Integer customid;

    @Column(name = "artid")
    private Integer artid;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "favori")
    private Integer favori;

    @Column(name = "visible")
    private Integer visible;

    // Constructeurs
    public ArtListImg() {
    }

    public ArtListImg(Integer customid, Integer artId, String image, Integer favori, Integer visible) {
        this.customid = customid;
        this.artid = artId;
        this.image = image;
        this.favori = favori;
        this.visible = visible;
    }

    // Getters et Setters
    public Integer getArtListImgId() {
        return artlistimgid;
    }

    public void setArtListImgId(Integer artlistimgid) {
        this.artlistimgid = this.artlistimgid;
    }

    public Integer getCustomid() {
        return customid;
    }

    public void setCustomid(Integer customid) {
        this.customid = customid;
    }

    public Integer getArtid() {
        return artid;
    }

    public void setArtId(Integer artid) {
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

    @Override
    public String toString() {
        return "ArtListImg{" +
                "artlistimgid=" + artlistimgid +
                ", customid=" + customid +
                ", artid=" + artid +
                ", image='" + image + '\'' +
                ", favori=" + favori +
                ", visible=" + visible +
                '}';
    }

    public List<Object> getDisplayData()
    {
        List<Object> row = new ArrayList<>();
        row.add(getArtListImgId());
        row.add(getCustomid());
        row.add(getImage());
        row.add(getFavori());
        row.add(getVisible());
        return row;
    }
}