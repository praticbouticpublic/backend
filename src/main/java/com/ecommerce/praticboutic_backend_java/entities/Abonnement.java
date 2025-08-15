package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Entité Abonnement
 */
@Entity
@Table(name = "abonnement")
public class Abonnement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aboid")
    private Integer aboId;

    @Column(name = "cltid", nullable = false)
    private Integer cltId;

    @Column(name = "creationboutic", nullable = false)
    private Boolean creationBoutic;

    @Column(name = "bouticid", nullable = false)
    private Integer bouticId;

    @Column(name = "stripe_subscription_id", nullable = false, length = 255)
    private String stripeSubscriptionId;

    @Column(name = "actif", nullable = false)
    private Integer actif;

    @Column(name = "metered", nullable = false, columnDefinition = "int unsigned NOT NULL DEFAULT '0'")
    private Integer metered = 0;

    // Relations (commentées, à activer si nécessaire)
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cltid", insertable = false, updatable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bouticid", insertable = false, updatable = false)
    private Boutique boutique;
    */

    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getAboId() {
        return aboId;
    }

    public void setAboId(Integer aboId) {
        this.aboId = aboId;
    }

    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getCltId() {
        return cltId;
    }

    public void setCltId(Integer clientId) {
        this.cltId = clientId;
    }

    public Boolean getCreationBoutic() {
        return creationBoutic;
    }

    public void setCreationBoutic(Boolean creationBoutic) {
        this.creationBoutic = creationBoutic;
    }

    public Integer getBouticId() {
        return bouticId;
    }

    public void setBouticId(Integer bouticId) {
        this.bouticId = bouticId;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public Integer getActif() {
        return actif;
    }

    public void setActif(Integer actif) {
        this.actif = actif;
    }

    public Integer getMetered() {
        return metered;
    }

    public void setMetered(Integer metered) {
        this.metered = metered;
    }

    /**
     * Vérifie si l'abonnement est actif
     * @return true si l'abonnement est actif, false sinon
     */
    public boolean isActif() {
        return actif != null && actif == 1;
    }

    /**
     * Vérifie si l'abonnement est de type mesuré (facturation à l'usage)
     * @return true si l'abonnement est mesuré, false sinon
     */
    public boolean isMetered() {
        return metered != null && metered == 1;
    }

    /**
     * Vérifie si cet abonnement a créé une boutique
     * @return true si une boutique a été créée avec cet abonnement
     */
    public boolean aBoutiqueCreee() {
        return creationBoutic != null && creationBoutic == true;
    }

    public void setTypePlan(String typePlan) {
    }

    public void setDateDebut(LocalDate dateDebut) {
    }

    public void setDateFin(LocalDate dateFin) {
    }
}
