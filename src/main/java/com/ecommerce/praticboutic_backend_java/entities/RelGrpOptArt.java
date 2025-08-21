package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relgrpoptart")
public class RelGrpOptArt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relgrpoartid")
    private Integer relgrpoartid;

    @Column(name = "customid", nullable = false)
    private Integer customid;

    @Column(name = "grpoptid", nullable = false, columnDefinition = "int DEFAULT 0")
    private Integer grpoptid = 0;

    @Column(name = "artid", nullable = false, columnDefinition = "int DEFAULT 0")
    private Integer artid = 0;

    @Column(name = "visible", nullable = false, columnDefinition = "int DEFAULT 1")
    private Integer visible = 1;

    // Relations (commentées, à activer si nécessaire)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grpoptid", insertable = false, updatable = false)
    private GroupeOpt groupeOpt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artid", insertable = false, updatable = false)
    private Article article;

    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getRelgrpoartid() {
        return relgrpoartid;
    }

    public void setRelgrpoartid(Integer relgrpoartid) {
        this.relgrpoartid = this.relgrpoartid;
    }


    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getCustomId() {
        return customid;
    }

    public void setCustomId(Integer customid) {
        this.customid = customid;
    }

    public Integer getGrpoptid() {
        return grpoptid;
    }

    public void setGrpoptid(Integer grpoptid) {
        this.grpoptid = grpoptid;
    }

    public Integer getArtid() {
        return artid;
    }

    public void setArtid(Integer artid) {
        this.artid = artid;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    /**
     * Méthode utilitaire pour vérifier si cette relation est visible
     * @return true si la relation est visible, false sinon
     */
    public boolean isVisible() {
        return visible != null && visible == 1;
    }

    public GroupeOpt getGroupeOpt() {
        return groupeOpt;
    }

    public void setGroupeOpt(GroupeOpt groupeOpt) {
        this.groupeOpt = groupeOpt;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public List<Object> getDisplayData()
    {
        List<Object> row = new ArrayList<>();
        row.add(getRelgrpoartid());
        row.add(getGroupeOpt().getNom());
        row.add(getArticle().getNom());
        row.add(getVisible().toString());
        return row;
    }
}