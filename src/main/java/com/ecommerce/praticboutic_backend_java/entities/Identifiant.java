package com.ecommerce.praticboutic_backend_java.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "identifiant")
public class Identifiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtid")
    private int idtid;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "hash", nullable = false, length = 32)
    private String hash;

    @Column(name = "actif", nullable = false)
    private int actif;

    // Default constructor
    public Identifiant() {}

    // Parameterized constructor
    public Identifiant(String email, String hash, int actif) {
        this.email = email;
        this.hash = hash;
        this.actif = actif;
    }

    // Getters and Setters
    public int getIdtid() {
        return idtid;
    }

    public void setIdtid(int idtid) {
        this.idtid = idtid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getActif() {
        return actif;
    }

    public void setActif(int actif) {
        this.actif = actif;
    }
}

