package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "COMMANDE")
public class Commande extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmdid")
    private Integer cmdid;

    @Column(name = "customid")
    private Integer customid;

    @Column(name = "numref", length = 60)
    private String numref;

    @Column(name = "nom", length = 60)
    private String nom;

    @Column(name = "prenom", length = 60)
    private String prenom;

    @Column(name = "telephone", length = 60)
    private String telephone;

    @Column(name = "adresse1", length = 150)
    private String adresse1;

    @Column(name = "adresse2", length = 150)
    private String adresse2;

    @Column(name = "codepostal", length = 5)
    private String codepostal;

    @Column(name = "ville", length = 50)
    private String ville;

    @Column(name = "vente", length = 45)
    private String vente;

    @Column(name = "paiement", length = 45)
    private String paiement;

    @Column(name = "sstotal")
    private Double sstotal = 0.0;

    @Column(name = "remise")
    private Double remise = 0.0;

    @Column(name = "fraislivraison")
    private Double fraislivraison = 0.0;

    @Column(name = "total")
    private Double total = 0.0;

    @Column(name = "commentaire", length = 300)
    private String commentaire;

    @Column(name = "method", length = 45)
    private String method;

    @Column(name = "`table`")
    private Integer table;

    @Column(name = "datecreation")
    private LocalDateTime dateCreation;

    @Column(name = "statid")
    private Integer statid;

    // Relation avec Categorie (si vous souhaitez conserver la relation JPA)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statid", insertable = false, updatable = false) // Utilisez insertable=false, updatable=false pour éviter les conflits
    private StatutCmd statutCmd;

    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)

    public Integer getCmdId() {
        return cmdid;
    }

    public void setCmdId(Integer cmdid) {
        this.cmdid = cmdid;
    }


    public Integer getCustomId() {
        return customid;
    }

    public void setCustomId(Integer customid) {
        this.customid = customid;
    }

    public String getNumRef() {
        return numref;
    }

    public void setNumRef(String numref) {
        this.numref = numref;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse1() {
        return adresse1;
    }

    public void setAdresse1(String adresse1) {
        this.adresse1 = adresse1;
    }

    public String getAdresse2() {
        return adresse2;
    }

    public void setAdresse2(String adresse2) {
        this.adresse2 = adresse2;
    }

    public String getCodePostal() {
        return codepostal;
    }

    public void setCodePostal(String codepostal) {
        this.codepostal = codepostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getVente() {
        return vente;
    }

    public void setVente(String vente) {
        this.vente = vente;
    }

    public String getPaiement() {
        return paiement;
    }

    public void setPaiement(String paiement) {
        this.paiement = paiement;
    }

    public Double getSsTotal() {
        return sstotal;
    }

    public void setSsTotal(Double sstotal) {
        this.sstotal = sstotal;
    }

    public Double getRemise() {
        return remise;
    }

    public void setRemise(Double remise) {
        this.remise = remise;
    }

    public Double getFraisLivraison() {
        return fraislivraison;
    }

    public void setFraisLivraison(Double fraislivraison) {
        this.fraislivraison = fraislivraison;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getTable() {
        return table;
    }

    public void setTable(Integer table) {
        this.table = table;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Integer getStatId() {
        return statid;
    }

    public void setStatId(Integer statid) {
        this.statid = statid;
    }

    public StatutCmd getStatutCmd() {
        return statutCmd;
    }

    public void setStatutCmd(StatutCmd statutCmd) {
        this.statutCmd = statutCmd;
    }

    public List<Object> getDisplayData()
    {
        List<Object> row = new ArrayList<>();
        row.add(getCmdId());
        row.add(getNumRef());
        row.add(getNom());
        row.add(getPrenom());
        row.add(getTelephone());
        row.add(getAdresse1());
        row.add(getAdresse2());
        row.add(getCodePostal());
        row.add(getVille());
        row.add(getVente());
        row.add(getPaiement());
        row.add(getSsTotal());
        row.add(getRemise());
        row.add(getFraisLivraison());
        row.add(getTotal());
        row.add(getCommentaire());
        row.add(getMethod());
        row.add(getTable());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss", Locale.FRENCH);
        String formatted = dateCreation.format(formatter);
        row.add(formatted);
        row.add(getStatutCmd().getEtat());
        return row;
    }

}