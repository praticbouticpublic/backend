package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Integer> {
    
    /**
     * Trouve les catégories pour une boutique donnée ou avec catid = 0
     *
     * @param customid L'identifiant de la boutique
     * @return Liste des catégories trouvées
     */
    List<Categorie> findByCustomidOrCatidOrderByCatid(Integer customid, Integer catid);
}