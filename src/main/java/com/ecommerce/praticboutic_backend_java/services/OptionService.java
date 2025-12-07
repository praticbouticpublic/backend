package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Option;
import com.ecommerce.praticboutic_backend_java.repositories.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service gérant les opérations liées aux options
 */
@Service
@Transactional
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;


    /**
     * Trouve une option par son identifiant
     * 
     * @param optId L'identifiant de l'option
     * @return L'option ou null si non trouvée
     */
    public Option findById(Integer optId) {
        return optionRepository.findById(optId).orElse(null);
    }
    
    /**
     * Sauvegarde une option
     * 
     * @param option L'option à sauvegarder
     * @return L'option sauvegardée
     */
    public Option save(Option option) {
        return optionRepository.save(option);
    }
    
    /**
     * Supprime une option
     * 
     * @param optId L'identifiant de l'option à supprimer
     */
    public void delete(Integer optId) {
        optionRepository.deleteById(optId);
    }
    

    public List<?> getOptions(Integer grpoptid) {
        return optionRepository.findByGrpoptid(grpoptid);
    }



}