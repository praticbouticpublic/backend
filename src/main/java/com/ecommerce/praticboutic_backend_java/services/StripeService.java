package com.ecommerce.praticboutic_backend_java.services;


import com.ecommerce.praticboutic_backend_java.configurations.StripeConfig;

import com.ecommerce.praticboutic_backend_java.exceptions.DatabaseException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.SubscriptionItemCollection;
import com.stripe.model.UsageRecord;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.UsageRecord;


import com.stripe.model.billing.MeterEvent;
import com.stripe.net.RequestOptions;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.UsageRecordCreateOnSubscriptionItemParams;
import com.stripe.param.billing.MeterEventCreateParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Service pour l'intégration avec Stripe
 */
@Service
public class StripeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    private final StripeConfig stripeConfig;

    @Autowired
    public StripeService(StripeConfig stripeConfig) {
        this.stripeConfig = stripeConfig;
        Stripe.apiKey = stripeConfig.getSecretKey();
        Stripe.setAppInfo(
                "pratic-boutic",
                "1.0.0",
                "https://praticboutic.fr"
        );
    }

    /**
     * Met à jour les métadonnées d'un abonnement Stripe
     */
    public void updateSubscriptionMetadata(String subscriptionId, Map<String, String> metadata) throws StripeException {
        try {
            SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                    .setMetadata(metadata)
                    .build();

            Subscription subscription = Subscription.retrieve(subscriptionId);
            subscription.update(params);

            logger.debug("Métadonnées Stripe mises à jour pour l'abonnement: {}", subscriptionId);
        } catch (StripeException e) {
            logger.error("Erreur Stripe lors de la mise à jour des métadonnées: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Mise à jour des métadonnées Stripe pour un abonnement
     */
    public void updateStripeSubscriptionMetadata(String subscriptionId, Integer abonnementId) throws StripeException {
        if (StringUtils.isEmpty(subscriptionId)) {
            throw new DatabaseException.InvalidSessionDataException("L'ID d'abonnement Stripe ne peut pas être vide");
        }

        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("pbabonumref", "ABOPB" + StringUtils.leftPad(abonnementId.toString(), 10, "0"));
            metadata.put("abonnement_id", abonnementId.toString());
            metadata.put("creation_date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            updateSubscriptionMetadata(subscriptionId, metadata);
            logger.info("Métadonnées Stripe mises à jour pour l'abonnement: {}", subscriptionId);
        } catch (StripeException e) {
            logger.error("Erreur lors de la mise à jour des métadonnées Stripe", e);
            throw e;
        }
    }

    /**
     * Enregistre un événement de mesure pour un client donné
     *
     * @param customId ID du client/boutique
     * @param sum Montant de base
     * @param discount Remise à appliquer
     * @param shippingCost Frais de livraison
     * @return boolean indiquant si l'événement de mesure a été créé
     * @throws Exception En cas d'erreur durant le processus
     */
    public boolean recordSubscriptionUsage(Integer customId, Double sum,
                                    Double discount, Double shippingCost) throws Exception {
        boolean urCreated = false;
        String query = "SELECT aboid, stripe_subscription_id FROM abonnement WHERE bouticid = ?";

        try {
            List<Map<String, Object>> subscriptions = jdbcTemplate.queryForList(query, customId);

            for (Map<String, Object> row : subscriptions) {
                if (!urCreated) {
                    String subscriptionId = (String) row.get("stripe_subscription_id");

                    try {
                        // Récupérer l'abonnement depuis Stripe
                        Subscription subscription = Subscription.retrieve(subscriptionId);

                        if ("active".equals(subscription.getStatus())) {
                            SubscriptionItemCollection subscriptionItems = subscription.getItems();

                            for (SubscriptionItem item : subscriptionItems.getData()) {
                                String usageType = item.getPrice().getRecurring().getUsageType();

                                if ("metered".equals(usageType)) {
                                    long usageQuantity = Math.round(sum - discount + shippingCost);
                                    long timestamp = Instant.now().getEpochSecond();
                                    String idempotencyKey = UUID.randomUUID().toString();

                                    UsageRecordCreateOnSubscriptionItemParams params =
                                            UsageRecordCreateOnSubscriptionItemParams.builder()
                                                    .setQuantity(usageQuantity)
                                                    .setTimestamp(timestamp)
                                                    .setAction(UsageRecordCreateOnSubscriptionItemParams.Action.SET)
                                                    .build();

                                    // Créer un RequestOptions avec l'idempotency_key
                                    RequestOptions requestOptions = RequestOptions.builder()
                                            .setIdempotencyKey(idempotencyKey)
                                            .build();

                                    // Création du Usage Record sur l'item de l'abonnement
                                    UsageRecord.createOnSubscriptionItem(item.getId(),  params, requestOptions);
                                    urCreated = true;
                                    break;
                                }
                            }
                        }
                    } catch (StripeException e) {
                        logger.info(e.getMessage());
                        // Passer à l'abonnement suivant
                    }
                }
            }
        } catch (DataAccessException e) {
            throw new Exception("Database error: " + e.getMessage());
        }

        return urCreated;
    }
}