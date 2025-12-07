package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "lIGNECMD", uniqueConstraints = @UniqueConstraint(name = "numref_UNIQUE", columnNames = {"customid", "cmdid", "ordre"}))
public class LigneCmd extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lignecmdid")
    private Integer lignecmdid;

    @Column(name = "customid", nullable = false)
    private Integer customid;

    @Column(name = "cmdid", nullable = false)
    private Integer cmdId;

    // Relations optionnelles avec l'entité Commande
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmdid", insertable = false, updatable = false)
    private Commande commande;

    @Column(name = "ordre", nullable = false)
    private Integer ordre;

    @Column(name = "type", nullable = false, length = 45)
    private String type;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "prix", nullable = false)
    private Float prix;

    @Column(name = "quantite", nullable = false)
    private Float quantite;

    @Column(name = "commentaire", nullable = false, length = 300)
    private String commentaire;

    @Column(name = "artid", nullable = false)
    private Integer artId;

    // Relation optionnelle avec l'entité Article
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artid", insertable = false, updatable = false)
    private Article article;

    @Column(name = "optid", nullable = false)
    private Integer optId;

    // Relation optionnelle avec l'entité Article
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optid", insertable = false, updatable = false)
    private Option option;

    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getLignecmdid() {
        return lignecmdid;
    }

    public void setLignecmdid(Integer lignecmdid) {
        this.lignecmdid = lignecmdid;
    }

    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getCustomId() {
        return customid;
    }

    public void setCustomId(Integer customid) {
        this.customid = customid;
    }

    public Integer getCmdId() {
        return cmdId;
    }

    public void setCmdId(Integer cmdId) {
        this.cmdId = cmdId;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Float getPrix() {
        return prix;
    }

    public void setPrix(Float prix) {
        this.prix = prix;
    }

    public Float getQuantite() {
        return quantite;
    }

    public void setQuantite(Float quantite) {
        this.quantite = quantite;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Integer getArtId() {
        return artId;
    }

    public void setArtId(Integer artId) {
        this.artId = artId;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Integer getOptId() {
        return optId;
    }

    public void setOptId(Integer optId) {
        this.optId = optId;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    /**
     * Calcule le montant total de la ligne de commande (prix * quantité)
     * @return le montant total
     */
    public Float calculerTotal() {
        if (prix != null && quantite != null) {
            return prix * quantite;
        }
        return 0.0f;
    }

    public List<Object> getDisplayData()
    {
        List<Object> row = new ArrayList<>();
        row.add(getLignecmdid());
        row.add(getCommande().getNumRef());
        row.add(getOrdre());
        row.add(getType());
        row.add(getNom());
        row.add(getPrix());
        row.add(getQuantite());
        row.add(getCommentaire());
        return row;
    }
}