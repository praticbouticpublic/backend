package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.GroupeOpt;
import com.ecommerce.praticboutic_backend_java.services.GroupeOptionService;
import com.ecommerce.praticboutic_backend_java.repositories.GroupeOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service gérant les opérations liées aux groupes d'options
 */
@Service
@Transactional
public class GroupeOptionService {

    @Autowired
    private GroupeOptionRepository groupeOptionRepository;


    /**
     * Trouve un groupe d'options par son identifiant
     * 
     * @param grpOptId L'identifiant du groupe d'options
     * @return Le groupe d'options ou null si non trouvé
     */
    public GroupeOpt findById(Integer grpOptId) {
        return groupeOptionRepository.findById(grpOptId).orElse(null);
    }
    
    /**
     * Sauvegarde un groupe d'options
     * 
     * @param groupeOption Le groupe d'options à sauvegarder
     * @return Le groupe d'options sauvegardé
     */
    public GroupeOpt save(GroupeOpt groupeOption) {
        return groupeOptionRepository.save(groupeOption);
    }
    
    /**
     * Supprime un groupe d'options
     * 
     * @param grpOptId L'identifiant du groupe d'options à supprimer
     */
    public void delete(Integer grpOptId) {
        groupeOptionRepository.deleteById(grpOptId);
    }
    


}