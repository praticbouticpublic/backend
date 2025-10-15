package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Identifiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdentifiantRepository extends JpaRepository<Identifiant, Integer> {
    // JpaRepository already provides methods like save, findById, delete, etc.
    // You can define custom query methods here if needed.
    List<Identifiant> findByEmail(String email);

}