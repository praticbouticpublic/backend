package com.ecommerce.praticboutic_backend_java.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "connexion")
public class Connexion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long connexid;
    
    @Column(name = "ip", nullable = false)
    private String ip;
    
    @Column(name = "ts", nullable = false)
    private LocalDateTime ts;
    
    // Getters et setters
    
    public Long getId() {
        return connexid;
    }
    
    public void setId(Long connexid) {
        this.connexid = connexid;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public LocalDateTime getTs() {
        return ts;
    }
    
    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }
}