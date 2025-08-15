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
public class BouticService{
    private static final Logger logger = LoggerFactory.getLogger(BouticService.class);
    public void updateSessionAfterBoutiqueCreation( Customer customer, String token)
    {
        Map<String, Object> payload = JwtService.parseToken(token).getClaims();
        payload.put("bo_stripe_customer_id", payload.get("registration_stripe_customer_id").toString());
        payload.put("bo_id", customer.getCustomId());
        payload.put("bo_email", payload.get("verify_email").toString());
        payload.put("bo_auth", "oui");
        payload.put("bo_init", "oui");
        payload.put("bo_customer", customer.getCustomer());
        payload.put("bo_creation_date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Pour des raisons de sécurité, nettoyage des données sensibles qui ne sont plus nécessaires
        payload.remove("registration_pass");

        logger.debug("Session mise à jour après création de la boutique");
    }
}

