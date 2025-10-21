package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.exceptions.DatabaseException;
import com.google.api.client.util.Value;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class SessionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    public void setSessionId(String sessionId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        request.changeSessionId(); // Méthode pour changer l'ID de session (peut varier selon la version de Spring)
    }

    public boolean hasAttribute(String name) {
        HttpSession session = getSession();
        return session.getAttribute(name) != null;
    }

    public Object getAttribute(String name) {
        HttpSession session = getSession();
        return session.getAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        HttpSession session = getSession();
        session.setAttribute(name, value);
    }

    private HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    public boolean isSessionValid(String sessionId) {
        // Implémentation pour vérifier la validité de la session
        return true; // À implémenter selon votre logique de gestion de session
    }

    public boolean isAuthenticated() {
        HttpSession session = getSession();
        return session.getAttribute("bo_auth") != null &&
                session.getAttribute("bo_auth").equals("oui");
    }

    public String getUserEmail() {
        HttpSession session = getSession();
        return (String) session.getAttribute("bo_email");
    }

    public void setBoId(Integer bouticId) {
        HttpSession session = getSession();
        session.setAttribute("bo_id", bouticId);
    }

    public void getBoId() {
        HttpSession session = getSession();
        session.getAttribute("bo_id");
    }


    public void updateSession(Map<String, Object> sessionData) {

        for (Map.Entry<String, Object> entry : sessionData.entrySet()) {
            getSession().setAttribute(entry.getKey(), entry.getValue());
        }

    }

    /**
     * Récupère un attribut de session sous forme de String
     */
    public String getSessionAttributeAsString(HttpSession session, String attributeName) {
        Object attribute = session.getAttribute(attributeName);
        return attribute != null ? attribute.toString() : null;
    }

    /**
     * Met à jour la session après la création d'une boutique
     */
    public void updateSessionAfterBoutiqueCreation(HttpSession session, Customer customer) {
        session.setAttribute("bo_stripe_customer_id", session.getAttribute("registration_stripe_customer_id"));
        session.setAttribute("bo_id", customer.getCustomId());
        session.setAttribute("bo_email", session.getAttribute("verify_email"));
        session.setAttribute("bo_auth", "oui");
        session.setAttribute("bo_init", "oui");
        session.setAttribute("bo_customer", customer.getCustomer());
        session.setAttribute("bo_creation_date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Pour des raisons de sécurité, nettoyage des données sensibles qui ne sont plus nécessaires
        session.removeAttribute("registration_pass");

        logger.debug("Session mise à jour après création de la boutique");
    }

    /**
     * Méthode pour vérifier la présence des données de session nécessaires à la création d'une boutique
     */
    public void validateSessionDataForBuildBoutic(HttpSession session) throws DatabaseException.InvalidSessionDataException {
        Map<String, String> requiredSessionAttributes = new HashMap<>();
        requiredSessionAttributes.put("verify_email", "Email de vérification");
        requiredSessionAttributes.put("registration_pass", "Mot de passe");
        requiredSessionAttributes.put("registration_qualite", "Qualité");
        requiredSessionAttributes.put("registration_nom", "Nom");
        requiredSessionAttributes.put("registration_prenom", "Prénom");
        requiredSessionAttributes.put("registration_adr1", "Adresse");
        requiredSessionAttributes.put("registration_cp", "Code postal");
        requiredSessionAttributes.put("registration_ville", "Ville");
        requiredSessionAttributes.put("registration_tel", "Téléphone");
        requiredSessionAttributes.put("registration_stripe_customer_id", "ID client Stripe");
        requiredSessionAttributes.put("initboutic_aliasboutic", "Alias de la boutique");
        requiredSessionAttributes.put("initboutic_nom", "Nom de la boutique");
        requiredSessionAttributes.put("initboutic_logo", "Logo");
        requiredSessionAttributes.put("initboutic_email", "Email de la boutique");
        requiredSessionAttributes.put("creationabonnement_stripe_subscription_id", "ID d'abonnement Stripe");
        requiredSessionAttributes.put("confboutic_validsms", "Validation par SMS");
        requiredSessionAttributes.put("confboutic_chxpaie", "Choix de paiement");
        requiredSessionAttributes.put("confboutic_chxmethode", "Méthode de livraison");
        requiredSessionAttributes.put("confboutic_mntmincmd", "Montant minimum de commande");

        List<String> missingAttributes = requiredSessionAttributes.entrySet().stream()
                .filter(entry -> session.getAttribute(entry.getKey()) == null)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (!missingAttributes.isEmpty()) {
            throw new DatabaseException.InvalidSessionDataException("Données de session manquantes: " + String.join(", ", missingAttributes));
        }
    }

    /**
     * Récupère la session HTTP courante
     */
    private HttpSession getCurrentSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

}