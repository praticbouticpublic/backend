package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Categorie;
import com.ecommerce.praticboutic_backend_java.repositories.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;

    /**
     * Récupère les catégories pour une boutique donnée
     * Correspond à la requête "categories" dans le fichier PHP
     * 
     * @param bouticId L'identifiant de la boutique
     * @return Liste des catégories sous forme de tableaux d'objets
     */
    public List<List<Object>> getCategories(Integer bouticId) {
        List<Categorie> categories = categorieRepository.findByCustomidOrCatidOrderByCatid(bouticId, 0);
        List<List<Object>> result = new ArrayList<>();
        
        for (Categorie categorie : categories) {
            List<Object> categorieArray = Arrays.asList(
                categorie.getCatid(),
                categorie.getNom(),
                categorie.getVisible()
            );
            result.add(categorieArray);
        }
        
        return result;
    }
    
    /**
     * Trouve une catégorie par son identifiant
     * 
     * @param catId L'identifiant de la catégorie
     * @return La catégorie trouvée ou null
     */
    public Categorie findById(Integer catId) {
        return categorieRepository.findById(catId).orElse(null);
    }
    
    /**
     * Sauvegarde une catégorie
     * 
     * @param categorie La catégorie à sauvegarder
     * @return La catégorie sauvegardée
     */
    public Categorie save(Categorie categorie) {
        return categorieRepository.save(categorie);
    }
    
    /**
     * Supprime une catégorie
     * 
     * @param catId L'identifiant de la catégorie à supprimer
     */
    public void delete(Integer catId) {
        categorieRepository.deleteById(catId);
    }
    
    /**
     * Vérifie si une catégorie existe
     * 
     * @param catId L'identifiant de la catégorie
     * @return true si la catégorie existe, false sinon
     */
    public boolean exists(Integer catId) {
        return categorieRepository.existsById(catId);
    }
}