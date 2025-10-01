package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Abonnement;
import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.exceptions.DatabaseException;
import com.ecommerce.praticboutic_backend_java.repositories.AbonnementRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

/**
 * Service gérant les opérations liées aux abonnements
 */
@Service
@Transactional
public class AbonnementService {

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private SessionService sessionService;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(AbonnementService.class);


    /**
     * Trouve un abonnement par son identifiant
     * 
     * @param abonnementId L'identifiant de l'abonnement
     * @return L'abonnement ou null si non trouvé
     */
    public Abonnement findById(Integer abonnementId) {
        return abonnementRepository.findById(abonnementId).orElse(null);
    }
    
    /**
     * Sauvegarde un abonnement
     * 
     * @param abonnement L'abonnement à sauvegarder
     * @return L'abonnement sauvegardé
     */
    public Abonnement save(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    /**
     * Crée un nouvel abonnement
     * 
     * @param bouticId L'identifiant de la boutique
     * @param typePlan Type de plan d'abonnement
     * @param dureeMonths Durée en mois
     * @return Le nouvel abonnement
     */
    public Abonnement createSubscription(Integer bouticId, String typePlan, int dureeMonths) {
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = dateDebut.plusMonths(dureeMonths);
        
        Abonnement abonnement = new Abonnement();
        abonnement.setBouticId(bouticId);
        abonnement.setTypePlan(typePlan);
        abonnement.setDateDebut(dateDebut);
        abonnement.setDateFin(dateFin);
        
        return abonnementRepository.save(abonnement);
    }

    public String getStripeCustomerId(Integer bouticid) {
        return null;
    }

    /**
     * Crée et sauvegarde un abonnement
     */
    public Abonnement createAndSaveAbonnement( Client client, Customer customer, String token)
            throws DataAccessException {

        Map<String, Object> payload = JwtService.parseToken(token).getClaims();
        String stripeSubscriptionId = payload.get("creationabonnement_stripe_subscription_id").toString();
        if (StringUtils.isEmpty(stripeSubscriptionId)) {
            throw new DatabaseException.InvalidSessionDataException("L'ID d'abonnement Stripe ne peut pas être vide");
        }

        Abonnement abonnement = new Abonnement();
        abonnement.setCltId(client.getCltId());
        abonnement.setCreationBoutic(false);
        abonnement.setBouticId(customer.getCustomId());
        abonnement.setStripeSubscriptionId(stripeSubscriptionId);
        abonnement.setActif(1);

        try {
            return abonnementRepository.save(abonnement);
        } catch (DataAccessException e) {
            logger.error("Erreur lors de la sauvegarde de l'abonnement", e);
            throw new DataAccessException("Erreur lors de la sauvegarde de l'abonnement", e) {};
        }
    }


}