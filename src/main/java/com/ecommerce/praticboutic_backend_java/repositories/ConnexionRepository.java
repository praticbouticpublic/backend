package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Connexion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConnexionRepository extends JpaRepository<Connexion, Long> {
    
    /**
     * Compte le nombre de tentatives depuis une adresse IP après une date donnée
     * @param ip adresse IP
     * @param date date limite
     * @return nombre de tentatives
     */
    int countByIpAndTsAfter(String ip, LocalDateTime date);
}