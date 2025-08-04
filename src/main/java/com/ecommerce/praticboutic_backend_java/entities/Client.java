package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

/**
 * Entité Client
 */
@Entity
@Table(name = "client")
public class Client extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cltid")
    private Integer cltid;
    @Column(name = "email")
    private String email;
    @Column(name = "pass")
    private String pass;
    @Column(name = "qualite")
    private String qualite;
    @Column(name = "nom")
    private String nom;
    @Column(name = "prenom")
    private String prenom;
    @Column(name = "adr1")
    private String adr1;
    @Column(name = "adr2")
    private String adr2;
    @Column(name = "cp")
    private String cp;
    @Column(name = "ville")
    private String ville;
    @Column(name = "tel")
    private String tel;
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    @Column(name = "actif")
    private Integer actif;
    @Column(name = "device_id")
    private String device_id;
    @Column(name = "device_type")
    private String device_type;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Customer> customers;

    /**
     * Constructeur par défaut sans arguments.
     * Obligatoire pour JPA.
     */
    public Client() {}

    /**
     * Constructeur avec tous les arguments.
     *
     * @param email l'adresse e-mail du client
     * @param pass le mot de passe du client
     * @param qualite la qualité du client (ex: M., Mme, etc.)
     * @param nom le nom de famille du client
     * @param prenom le prénom du client
     * @param adr1 l'adresse principale du client
     * @param adr2 l'adresse secondaire du client
     * @param cp le code postal du client
     * @param ville la ville du client
     * @param tel le numéro de téléphone du client
     * @param stripeCustomerId l'identifiant Stripe du client
     * @param actif l'état d'activation du compte client
     * @param device_id l'identifiant de l'appareil du client
     * @param device_type le type d'appareil du client
     */
    public Client(String email, String pass, String qualite, String nom, String prenom, String adr1,
                  String adr2, String cp, String ville, String tel, String stripeCustomerId,
                  Integer actif, String device_id, String device_type) {
        this.email = email;
        this.pass = pass;
        this.qualite = qualite;
        this.nom = nom;
        this.prenom = prenom;
        this.adr1 = adr1;
        this.adr2 = adr2;
        this.cp = cp;
        this.ville = ville;
        this.tel = tel;
        this.stripeCustomerId = stripeCustomerId;
        this.actif = actif;
        this.device_id = device_id;
        this.device_type = device_type;
    }

    /**
     * Constructeur avec les informations essentielles pour la création d'un client.
     *
     * @param email l'adresse e-mail du client
     * @param pass le mot de passe du client
     * @param nom le nom de famille du client
     * @param prenom le prénom du client
     */
    public Client(String email, String pass, String nom, String prenom) {
        this.email = email;
        this.pass = pass;
        this.nom = nom;
        this.prenom = prenom;
        this.actif = 1;
    }

    /**
     * Constructeur avec les informations d'identification et la qualité.
     *
     * @param email l'adresse e-mail du client
     * @param pass le mot de passe du client
     * @param qualite la qualité du client (ex: M., Mme, etc.)
     * @param nom le nom de famille du client
     * @param prenom le prénom du client
     */
    public Client(String email, String pass, String qualite, String nom, String prenom) {
        this.email = email;
        this.pass = pass;
        this.qualite = qualite;
        this.nom = nom;
        this.prenom = prenom;
        this.actif = 1;
    }

    // Getters et Setters (sans les accesseurs pour id qui sont dans BaseEntity)
    public Integer getCltId() {
        return cltid;
    }

    public void setCltId(Integer clientId) {
        this.cltid = clientId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getQualite() {
        return qualite;
    }

    public void setQualite(String qualite) {
        this.qualite = qualite;
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

    public String getAdr1() {
        return adr1;
    }

    public void setAdr1(String adr1) {
        this.adr1 = adr1;
    }

    public String getAdr2() {
        return adr2;
    }

    public void setAdr2(String adr2) {
        this.adr2 = adr2;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public Integer isActif() {
        return this.actif;
    }

    public void setActif(Integer actif) {
        this.actif = actif;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public boolean isPresent() {
        return (this.cltid != null) && (this.cltid != 0);
    }

}
