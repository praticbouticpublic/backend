package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

/**
 * Entité Customer (boutique)
 */
@Entity
@Table(name = "customer")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customid;

    @Column(name = "cltid", unique = true)
    private Integer cltid;

    @Column(length = 100)
    private String customer;

    @Column(length = 100)
    private String nom;

    @Column(length = 100)
    private String logo;

    @Column(length = 255)
    private String courriel;

    @Column(name = "actif")
    private Integer actif;

    // Relation avec l'entité Client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cltid", referencedColumnName = "cltid", insertable = false, updatable = false)
    private Client client;




    /**
     * Constructeur par défaut sans arguments.
     * Obligatoire pour JPA.
     */
    public Customer() {}

    /**
     * Constructeur avec tous les arguments.
     *
     * @param cltid l'identifiant du client lié
     * @param customer l'alias du client
     * @param nom le nom du client
     * @param logo le logo du client
     * @param courriel l'adresse courriel du client
     */
    public Customer(Integer cltid, String customer, String nom, String logo, String courriel) {
        this.cltid = cltid;
        this.customer = customer;
        this.nom = nom;
        this.logo = logo;
        this.courriel = courriel;
    }

    /**
     * Constructeur avec les informations essentielles.
     *
     * @param cltid l'identifiant du client lié
     * @param customer l'alias du client
     * @param nom le nom du client
     */
    public Customer(Integer cltid, String customer, String nom) {
        this.cltid = cltid;
        this.customer = customer;
        this.nom = nom;
    }

    /**
     * Constructeur de création d'une boutique pour un client existant.
     * Génère automatiquement un alias à partir du nom.
     *
     * @param cltid l'identifiant du client lié
     * @param nom le nom du client/de la boutique
     * @param courriel l'adresse courriel de contact de la boutique
     * @param generateAlias paramètre indiquant qu'il faut générer un alias (distingue ce constructeur de l'autre à 3 paramètres)
     */
    public Customer(Integer cltid, String nom, String courriel, boolean generateAlias) {
        this.cltid = cltid;
        this.nom = nom;
        this.courriel = courriel;
        if (generateAlias) {
            // L'alias par défaut est dérivé du nom
            this.customer = nom.toLowerCase().replaceAll("\\s+", "-");
        }
    }
    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getCustomId() {
        return customid;
    }

    public void setCustomId(Integer customid) {
        this.customid = customid;
    }

    /**
     * Retourne l'identifiant du client lié.
     * @return l'identifiant du client lié
     */
    public Integer getCltid() {
        return cltid;
    }

    /**
     * Définit l'identifiant du client lié.
     * @param cltid le nouvel identifiant du client lié
     */
    public void setCltid(Integer cltid) {
        this.cltid = cltid;
    }

    /**
     * Retourne l'alias du client.
     * @return l'alias du client
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Définit l'alias du client.
     * @param customer le nouvel alias du client
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    /**
     * Retourne le nom du client.
     * @return le nom du client
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom du client.
     * @param nom le nouveau nom du client
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le logo du client.
     * @return le logo du client
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Définit le logo du client.
     * @param logo le nouveau logo du client
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * Retourne l'adresse courriel du client.
     * @return l'adresse courriel du client
     */
    public String getCourriel() {
        return courriel;
    }

    /**
     * Définit l'adresse courriel du client.
     * @param courriel la nouvelle adresse courriel du client
     */
    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    // getters et setters
    public Integer getActif() {
        return actif;
    }

    public void setActif(Integer actif) {
        this.actif = actif;
    }

    public String getStripeCustomerId() {
        // Si la relation n'est pas chargée ou si le client associé est null
        if (this.client == null) {
            return null;
        }

        // Retourne la valeur stripe_customer_id du client associé
        return this.client.getStripeCustomerId();
    }

    public Client getClient(){
        return this.client;
    }
    public void setClient(Client client){
        this.client = client;
    }


    public boolean isPresent() {
        return (this.customid != null) && (this.customid != 0);
    }
}
