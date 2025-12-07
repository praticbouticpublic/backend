package com.ecommerce.praticboutic_backend_java.controllers;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.SubscriptionListParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostConstruct
    public void init() {
        // Initialize Stripe configuration

        Stripe.apiKey = stripeApiKey;


        // For sample support and debugging (not required for production)
        Stripe.setAppInfo(
                "pratic-boutic/registration",
                "0.0.2",
                "https://praticboutic.fr"
        );
    }

    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Vérification de la signature
            Event event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );

            // Log de l’événement
            logger.info("Webhook Stripe reçu : type={}, id={}", event.getType(), event.getId());

            // On s'intéresse aux abonnements créés ou supprimés
            if ("customer.subscription.deleted".equals(event.getType()) ||
                    "customer.subscription.created".equals(event.getType())) {

                EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

                Subscription subscription;

                if (deserializer.getObject().isPresent()) {
                    subscription = (Subscription) deserializer.getObject().get();
                } else {
                    // Fallback si la désérialisation automatique échoue
                    String rawJson = deserializer.getRawJson();
                    subscription = ApiResource.GSON.fromJson(rawJson, Subscription.class);
                }

                // Traitement de l'abonnement
                updateCustomerSubscription(subscription);
            }

            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            logger.error("Signature Stripe invalide", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature invalide");

        } catch (Exception e) {
            logger.error("Erreur dans le traitement du webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    private void updateCustomerSubscription(Subscription subscription) {
        try {
            String customerId = subscription.getCustomer();

            // Check active subscriptions
            SubscriptionListParams params = SubscriptionListParams.builder()
                    .setCustomer(customerId)
                    .setStatus(SubscriptionListParams.Status.ACTIVE)
                    .build();

            SubscriptionCollection subscriptions = Subscription.list(params);
            Integer bouticId;
            try
            {

                // Get the boutic ID from the database
                bouticId = jdbcTemplate.queryForObject(
                        "SELECT customer.customid FROM customer, client WHERE client.stripe_customer_id = ? AND customer.cltid = client.cltid",
                        Integer.class,
                        customerId
                );
            }
            catch (EmptyResultDataAccessException e) {
                bouticId = null;
            }

            if (bouticId == null) {
                logger.warn("No customer found for Stripe customer ID: {}", customerId);
                return;
            }

            // Update customer active status based on subscription status
            boolean hasActiveSubscription = !subscriptions.getData().isEmpty();
            String query = "UPDATE customer SET actif = ? WHERE customid = ?";

            int updated = jdbcTemplate.update(query, hasActiveSubscription ? 1 : 0, bouticId);

            if (updated == 0) {
                logger.warn("No customer record updated for bouticId: {}", bouticId);
            }

        } catch (Exception e) {
            logger.error("Error updating subscription for customer: {}", subscription.getId(), e);
        }
    }
}
