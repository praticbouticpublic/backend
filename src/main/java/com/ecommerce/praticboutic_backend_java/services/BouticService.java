package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Customer;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
// ... existing code ...
public class BouticService{
    private static final Logger logger = LoggerFactory.getLogger(BouticService.class);

    private final HttpSession httpSession;

    public BouticService(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void updateSessionAfterBoutiqueCreation(Customer customer, String token)
    {
        if (customer == null) {
            logger.debug("updateSessionAfterBoutiqueCreation ignoré: customer null");
            return;
        }

        // Si token null/blank: poser seulement les attributs basiques
        if (token == null || token.isBlank()) {
            // poser les attributs basiques attendus par les tests
            httpSession.setAttribute("customer_id", customer.getCustomId());
            httpSession.setAttribute("customer_alias", customer.getCustomer());
            logger.debug("Session mise à jour (token absent): id et alias posés");
            return;
        }

        Map<String, Object> payload;
        try {
            payload = JwtService.parseToken(token).getClaims();
        } catch (RuntimeException ex) {
            // Token invalide: poser seulement les attributs basiques, pas d’auth_token
            logger.warn("Token JWT invalide, on pose uniquement id et alias: {}", ex.getMessage());
            httpSession.setAttribute("customer_id", customer.getCustomId());
            httpSession.setAttribute("customer_alias", customer.getCustomer());
            return;
        }

        payload.put("bo_stripe_customer_id", String.valueOf(payload.get("registration_stripe_customer_id")));
        payload.put("bo_id", customer.getCustomId());
        payload.put("bo_email", payload.get("verify_email"));
        payload.put("bo_auth", "oui");
        payload.put("bo_init", "oui");
        payload.put("bo_customer", customer.getCustomer());
        payload.put("bo_creation_date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        payload.remove("registration_pass");

        httpSession.setAttribute("customer_id", customer.getCustomId());
        httpSession.setAttribute("customer_alias", customer.getCustomer());
        httpSession.setAttribute("auth_token", token);

        logger.debug("Session mise à jour après création de la boutique (token valide)");
    }
}
// ... existing code ...


