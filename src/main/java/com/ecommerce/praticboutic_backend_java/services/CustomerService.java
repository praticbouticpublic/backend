package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.exceptions.DatabaseException;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;

import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service gérant les opérations liées aux boutiques
 */
@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SessionService sessionService;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);


    /**
     * Récupère les informations d'une boutique
     * 
     * @param strCustomer L'identifiant de la boutique
     * @return Map contenant les informations de la boutique
     */
    public List<?> getBouticInfo(String strCustomer) {
        Customer boutic = customerRepository.findByCustomer(strCustomer);
        if (boutic.isPresent()) {
            return List.of(boutic.getCustomId(), boutic.getLogo(), boutic.getNom());
        }
        return null;
    }
    
    /**
     * Trouve une boutique par son identifiant
     * 
     * @param bouticId L'identifiant de la boutique
     * @return La boutique ou null si non trouvée
     */
    public Customer findById(Integer bouticId) {
        return customerRepository.findById(bouticId).orElse(null);
    }
    
    /**
     * Sauvegarde une boutique
     * 
     * @param boutic La boutique à sauvegarder
     * @return La boutique sauvegardée
     */
    public Customer save(Customer boutic) {
        return customerRepository.save(boutic);
    }
    
    /**
     * Met à jour l'état d'ouverture d'une boutique
     * 
     * @param bouticId L'identifiant de la boutique
     * @param ouvert Nouvel état d'ouverture
     * @return La boutique mise à jour ou null si non trouvée
     */

    /**
     * Récupère toutes les boutiques
     * 
     * @return Liste des boutiques
     */
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    
    /**
     * Récupère toutes les boutiques ouvertes
     * 
     * @return Liste des boutiques ouvertes
     */
    public List<Customer> findAllOpen() {
        return customerRepository.findByActif(1);
    }
    
    public List<Customer> findByActif(Integer actif) {
        return customerRepository.findByActif(1);
    }

    public Customer createAndSaveCustomer(Client client, String token)
            throws DatabaseException.InvalidAliasException, DataAccessException {

        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        String aliasBoutic = payload.get("initboutic_aliasboutic").toString() ;
        if (StringUtils.isEmpty(aliasBoutic)) {
            throw new DatabaseException.InvalidAliasException("L'identifiant de la boutique ne peut pas être vide");
        }

        // Validation du format de l'alias (lettres, chiffres et tirets uniquement)
        if (!aliasBoutic.matches("^[a-zA-Z0-9-]+$")) {
            throw new DatabaseException.InvalidAliasException("L'identifiant ne peut contenir que des lettres, des chiffres et des tirets");
        }

        // Vérification des identifiants interdits
        List<String> forbiddenIds = Arrays.asList("admin", "common", "upload", "vendor", "api", "assets",
                "static", "media", "support", "help", "login", "register", "system");
        if (forbiddenIds.contains(aliasBoutic.toLowerCase())) {
            throw new DatabaseException.InvalidAliasException("Cet identifiant n'est pas autorisé: " + aliasBoutic);
        }

        // Vérification de l'unicité de l'alias (à implémenter dans le repository)
        /*Optional<Customer> existingCustomer = customerRepository.findByCustomerIgnoreCase(aliasBoutic);
        if (existingCustomer.isPresent()) {
            throw new InvalidAliasException("Cet identifiant de boutique est déjà utilisé: " + aliasBoutic);
        }*/

        // Création de la boutique
        Customer customer = new Customer();
        customer.setCltid(client.getCltId());
        customer.setCustomer(aliasBoutic);
        customer.setNom(payload.get("initboutic_nom").toString());
        customer.setLogo(payload.get("initboutic_logo").toString());
        customer.setCourriel(payload.get("initboutic_email").toString());
        customer.setActif(1);

        try {
            return customerRepository.save(customer);
        } catch (DataAccessException e) {
            logger.error("Erreur lors de la sauvegarde de la boutique", e);
            throw new DataAccessException("Erreur lors de la sauvegarde de la boutique", e) {};
        }
    }
}