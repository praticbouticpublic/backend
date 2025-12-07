package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.GroupeOpt;
import com.ecommerce.praticboutic_backend_java.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface de repository pour les opérations de base de données sur les entités GroupeOption
 */
@Repository
public interface GroupeOptionRepository extends JpaRepository<GroupeOpt, Integer> {


}