package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.StatutCmd;
import com.ecommerce.praticboutic_backend_java.repositories.StatutCmdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StatutCmdService {

    @Autowired
    private StatutCmdRepository statutCmdRepository;
    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(StatutCmdService.class);
    /**
     * Méthode pour créer les statuts de commande par défaut
     */
    public void createDefaultOrderStatuses(Integer customId) {
        List<StatutCmd> statuts = Arrays.asList(
                new StatutCmd(customId, "Commande à faire", "#E2001A",
                        "Bonjour, votre commande a été transmise. %boutic% vous remercie et vous tiendra informé de son avancement.", 1, 1),
                new StatutCmd(customId, "En cours de préparation", "#EB690B",
                        "Votre commande est en cours de préparation.", 0, 1),
                new StatutCmd(customId, "En cours de livraison", "#E2007A",
                        "Votre commande est en cours de livraison.", 0, 1),
                new StatutCmd(customId, "Commande à disposition", "#009EE0",
                        "Votre commande est à disposition.", 0, 1),
                new StatutCmd(customId, "Commande terminée", "#009036",
                        "%boutic% vous remercie pour votre commande. À très bientôt.", 0, 1),
                new StatutCmd(customId, "Commande annulée", "#1A171B",
                        "Nous ne pouvons donner suite à votre commande. Pour plus d'informations, merci de nous contacter.", 0, 1)
        );

        try {
            statutCmdRepository.saveAll(statuts);
            logger.debug("Statuts de commande par défaut créés pour la boutique: {}", customId);
        } catch (DataAccessException e) {
            logger.error("Erreur lors de la création des statuts de commande par défaut", e);
            throw e;
        }
    }
}
