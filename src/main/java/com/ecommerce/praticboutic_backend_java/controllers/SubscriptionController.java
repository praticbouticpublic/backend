package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.configurations.StripeConfig;
import com.ecommerce.praticboutic_backend_java.entities.Abonnement;
import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.repositories.AbonnementRepository;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;

import com.ecommerce.praticboutic_backend_java.requests.LiensRequest;
import com.ecommerce.praticboutic_backend_java.requests.LoginRequest;
import com.ecommerce.praticboutic_backend_java.requests.SubscriptionRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.SubscriptionListParams;
import jakarta.servlet.http.HttpSession;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    private final StripeConfig stripeConfig;

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private Environment environment;  // Pour accéder aux variables d'environnement

    @Autowired
    DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public SubscriptionController(StripeConfig stripeConfig) {
        this.stripeConfig = stripeConfig;
    }

    /**
     * Récupère les liens de création de boutique pour un client authentifié
     *
     * @param loginRequest La requête contenant le login du client
     * @return Liste des abonnements avec leurs informations Stripe
     * @throws Exception Si le client n'est pas authentifié ou n'existe pas
     */
    @PostMapping("/liens-creation-boutic")
    public ResponseEntity<?> getLiensCreationBoutic(@RequestBody LiensRequest loginRequest, @RequestHeader("Authorization") String authHeader) throws Exception {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        // Vérification de l'authentification
        if (payload.get("bo_auth") == null || !payload.get("bo_auth").equals("oui")) {
            throw new Exception("Non authentifié");
        }

        // Récupération du client par son email
        Optional<Client> client = clientRepository.findByEmailAndActif(loginRequest.getLogin(), 1);
        if (client.isEmpty() || client.get().getStripeCustomerId() == null || client.get().getStripeCustomerId().isEmpty()) {
            throw new Exception("Erreur ! Client non trouvé");
        }

        // Récupération des abonnements du client
        List<Abonnement> abonnements = abonnementRepository.findByCltId(client.get().getCltId());

        // Préparation de la réponse
        List<Map<String, Object>> liensCreation = new ArrayList<>();

        for (Abonnement abonnement : abonnements) {
            try {
                // Récupération des informations de souscription depuis Stripe
                Subscription subscription =
                        stripeConfig.getStripeClient().subscriptions().retrieve(abonnement.getStripeSubscriptionId());

                if (subscription != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("aboid", abonnement.getAboId());
                    item.put("creationboutic", abonnement.getCreationBoutic());
                    item.put("bouticid", abonnement.getBouticId());
                    item.put("stripe_subscription", subscription.toJson());

                    liensCreation.add(item);
                }
            } catch (Exception e) {
                // Ne rien faire en cas d'erreur (comme dans le code PHP original)
                logger.debug(() -> "Erreur lors de la récupération de l'abonnement Stripe: " + e.getMessage());
            }
        }
        response.put("status", "OK");
        response.put("data", liensCreation);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère la configuration Stripe nécessaire pour le frontend
     *

     * @return Un objet contenant la clé publique Stripe et les tarifs disponibles
     * @throws Exception Si l'email n'est pas vérifié
     */
    @PostMapping("/configuration")
    public Map<String, Object> getConfiguration(@RequestHeader("Authorization") String authHeader) throws Exception {
        // Vérifier que l'email a été vérifié
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        if (payload.get("verify_email") == null) {
            throw new Exception("Courriel non vérifié");
        }

        // Récupérer les prix depuis Stripe

        Map<String, Object> params = new HashMap<>();
        params.put("lookup_keys", Arrays.asList("pb_fixe", "pb_conso"));

        PriceCollection prices = Price.list(params);


        // Construire la réponse
        Map<String, Object> output = new HashMap<>();
        output.put("publishableKey", stripePublicKey);
        output.put("prices", prices.getData());

        return output;
    }

    /**
     * Récupère la configuration Stripe pour l'interface d'administration
     *

     * @return Un objet contenant la clé publique Stripe et les tarifs disponibles
     * @throws Exception Si l'utilisateur n'est pas authentifié en tant qu'administrateur
     */
    @PostMapping("/boconfiguration")
    public Map<String, Object> getBackOfficeConfiguration(@RequestHeader("Authorization") String authHeader) throws Exception {
        // Vérifier l'authentification admin
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (payload.get("bo_auth") == null || !"oui".equals(payload.get("bo_auth"))) {
            throw new Exception("Non authentifié");
        }

        // Récupérer la clé publique depuis les variables d'environnement
        //String publishableKey = environment.getProperty("STRIPE_PUBLISHABLE_KEY");

        // Récupérer les prix depuis Stripe
        Stripe.apiKey = stripeSecretKey;
        Map<String, Object> params = new HashMap<>();
        params.put("lookup_keys", Arrays.asList("pb_fixe", "pb_conso"));

        PriceCollection prices = Price.list(params);

        // Construire la réponse
        Map<String, Object> output = new HashMap<>();
        output.put("publishableKey", stripePublicKey);
        output.put("prices", prices.getData());

        return output;
    }

    /**
     * Crée un abonnement Stripe pour l'utilisateur connecté
     *
     * @param request Objet contenant les paramètres de création d'abonnement

     * @return Les informations sur l'abonnement créé
     * @throws Exception Si l'email n'est pas vérifié ou si une erreur survient
     */
    @PostMapping("/creationabonnement")
    public Map<String, String> createSubscription(@RequestBody SubscriptionRequest request, @RequestHeader("Authorization") String authHeader) throws Exception {
        // Vérifier que l'email a été vérifié
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (payload.get("verify_email").toString() == null) {
            throw new Exception("Courriel non vérifié");
        }

        // Stocker les informations dans la session
        payload.put("creationabonnement_priceid", request.getPriceid());

        // Récupérer l'ID client Stripe
        String stripeCustomerId = payload.get("registration_stripe_customer_id").toString();

        Stripe.apiKey = stripeSecretKey;

        // Créer l'abonnement
        Map<String, Object> item = new HashMap<>();
        item.put("price", request.getPriceid());

        Map<String, Object> params = new HashMap<>();
        params.put("customer", stripeCustomerId);
        params.put("items", Collections.singletonList(item));
        params.put("payment_behavior", "default_incomplete");
        params.put("expand", Collections.singletonList("latest_invoice.payment_intent"));

        Subscription subscription = Subscription.create(params);

        // Stocker l'ID d'abonnement dans la session
        payload.put("creationabonnement_stripe_subscription_id", subscription.getId());

        // Construire la réponse
        Map<String, String> output = new HashMap<>();
        output.put("subscriptionId", subscription.getId());

        // Récupérer le client secret
        Invoice invoice = (Invoice) subscription.getLatestInvoiceObject();
        PaymentIntent intent = (PaymentIntent) invoice.getPaymentIntentObject();
        String jwt = JwtService.generateToken(payload, "" );
        output.put("token", jwt);
        output.put("clientSecret", intent.getClientSecret());

        return output;
    }

    /**
     * Crée un abonnement Stripe depuis l'interface d'administration
     *
     * @param request Objet contenant les paramètres de création d'abonnement

     * @return Les informations sur l'abonnement créé
     * @throws Exception Si l'utilisateur n'est pas authentifié en tant qu'administrateur ou si une erreur survient
     */
    @PostMapping("/bocreationabonnement")
    public Map<String, String> createBackOfficeSubscription(@RequestBody SubscriptionRequest request, @RequestHeader("Authorization") String authHeader) throws Exception {
        // Vérifier l'authentification admin
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (payload.get("bo_auth") == null || !"oui".equals(payload.get("bo_auth"))) {
            throw new Exception("Non authentifié");
        }

        // Stocker les informations dans la session
        payload.put("bocreationabonnement_priceid", request.getPriceid());

        // Récupérer l'ID client Stripe
        String stripeCustomerId = payload.get("bo_stripe_customer_id").toString();

        // Vérifier que le client existe en base de données
        Integer clientId = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT cltid FROM client WHERE stripe_customer_id = ? AND actif = 1")) {
            stmt.setString(1, stripeCustomerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    clientId = rs.getInt("cltid");
                }
            }
        }

        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            throw new Exception("Erreur ! Client non trouvé");
        }

        Stripe.apiKey = stripeSecretKey;

        // Créer l'abonnement
        Map<String, Object> item = new HashMap<>();
        item.put("price", request.getPriceid());

        Map<String, Object> params = new HashMap<>();
        params.put("customer", stripeCustomerId);
        params.put("items", Collections.singletonList(item));
        params.put("payment_behavior", "default_incomplete");
        params.put("expand", Collections.singletonList("latest_invoice.payment_intent"));

        Subscription subscription = Subscription.create(params);

        // Insérer l'abonnement en base de données
        Integer abonnementId = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO abonnement(cltid, creationboutic, bouticid, stripe_subscription_id, actif) VALUES (?, 0, ?, ?, 1)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, Integer.parseInt(payload.get("bo_id").toString()));
            stmt.setString(3, subscription.getId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    abonnementId = rs.getInt(1);
                }
            }
        }

        // Mettre à jour les métadonnées de l'abonnement Stripe
        String reference = "ABOPB" + String.format("%010d", abonnementId);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("pbabonumref", reference);

        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("metadata", metadata);

        Subscription updatedSubscription = subscription.update(updateParams);

        // Stocker l'ID d'abonnement dans la session
        payload.put("bocreationabonnement_stripe_subscription_id", subscription.getId());

        // Construire la réponse
        Map<String, String> output = new HashMap<>();
        output.put("subscriptionId", subscription.getId());

        // Récupérer le client secret
        Invoice invoice = (Invoice) subscription.getLatestInvoiceObject();
        PaymentIntent intent = (PaymentIntent) invoice.getPaymentIntentObject();
        String jwt = JwtService.generateToken(payload, "" );
        output.put("token", jwt);
        output.put("clientSecret", intent.getClientSecret());

        return output;
    }

    /**
     * Gère la sélection d'un abonnement consommation par l'utilisateur
     *
     * @param requestData Données de la requête contenant l'action et l'ID du prix

     * @return Un objet contenant l'ID client et l'ID du prix
     * @throws Exception Si l'utilisateur n'est pas authentifié ou son email non vérifié
     */
    @PostMapping("/conso")
    public Map<String, Object> handleConsommationSelection(@RequestBody Map<String, Object> requestData, @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (!"conso".equals(requestData.get("action"))) {
            throw new Exception("Action invalide");
        }

        if (payload.get("verify_email") == null) {
            throw new Exception("Courriel non vérifié");
        }

        String priceId = (String) requestData.get("priceid");
        payload.put("creationabonnement_priceid", priceId);

        String stripeCustomerId = payload.get("registration_stripe_customer_id").toString();

        Map<String, Object> output = new HashMap<>();
        String jwt = JwtService.generateToken(payload, "" );
        output.put("token", jwt);
        output.put("customerId", stripeCustomerId);
        output.put("priceId", priceId);

        return output;
    }

    /**
     * Gère la sélection d'un abonnement consommation par l'administrateur backoffice
     *
     * @param requestData Données de la requête contenant l'action et l'ID du prix

     * @return Un objet contenant l'ID client et l'ID du prix
     * @throws Exception Si l'administrateur n'est pas authentifié
     */
    @PostMapping("/boconso")
    public Map<String, Object> handleBackOfficeConsommationSelection(@RequestBody Map<String, Object> requestData,
                                                                     @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        if (!"boconso".equals(requestData.get("action"))) {
            throw new Exception("Action invalide");
        }

        if (payload.get("bo_auth").toString() == null || !"oui".equals(payload.get("bo_auth").toString())) {
            throw new Exception("Non authentifié");
        }

        String priceId = requestData.get("priceid").toString();
        payload.put("bocreationabonnement_priceid", priceId);
        String jwt = JwtService.generateToken(payload, "" );

        String stripeCustomerId = payload.get("bo_stripe_customer_id").toString();

        Map<String, Object> output = new HashMap<>();
        output.put("token", jwt);
        output.put("customerId", stripeCustomerId);
        output.put("priceId", priceId);

        return output;
    }

    /**
     * Crée un abonnement consommation pour un utilisateur
     *
     * @param requestData Données de la requête avec les informations d'abonnement

     * @return L'objet subscription créé dans Stripe
     * @throws Exception Si l'utilisateur n'est pas authentifié ou si la création échoue
     */
    @PostMapping("/consocreationabonnement")
    ResponseEntity<?> createConsommationSubscription(@RequestBody Map<String, Object> requestData, @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        if (!"consocreationabonnement".equals(requestData.get("action"))) {
            throw new Exception("Action invalide");
        }

        if (payload.get("verify_email").toString() == null) {
            throw new Exception("Courriel non vérifié");
        }

        String stripeCustomerId = (String) requestData.get("customerId");
        String priceId = (String) requestData.get("priceId");
        String paymentMethodId = (String) requestData.get("paymentMethodId");

        payload.put("creationabonnement_priceid", priceId);

        // Configurer Stripe
        Stripe.apiKey = stripeSecretKey;

        try {
            // Attacher la méthode de paiement au client
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            Map<String, Object> paymentMethodParams = new HashMap<>();
            paymentMethodParams.put("customer", stripeCustomerId);
            paymentMethod.attach(paymentMethodParams);

            // Définir la méthode de paiement par défaut
            Map<String, Object> customerParams = new HashMap<>();
            Map<String, Object> invoiceSettings = new HashMap<>();
            invoiceSettings.put("default_payment_method", paymentMethodId);
            customerParams.put("invoice_settings", invoiceSettings);
            Customer.retrieve(stripeCustomerId).update(customerParams);

            // Créer l'abonnement
            Map<String, Object> item = new HashMap<>();
            item.put("price", priceId);

            Map<String, Object> subscriptionParams = new HashMap<>();
            subscriptionParams.put("customer", stripeCustomerId);
            subscriptionParams.put("items", Collections.singletonList(item));
            subscriptionParams.put("expand", Collections.singletonList("latest_invoice.payment_intent"));

            Subscription subscription = Subscription.create(subscriptionParams);

            payload.put("creationabonnement_stripe_subscription_id", subscription.getId());

            // Convertir l'objet Subscription en Map via Gson
            Gson gson = new Gson();
            String jsonString = gson.toJson(subscription);
            Map<String, Object> subscriptionMap = gson.fromJson(jsonString, Map.class);
            Map<String, Object> output = new HashMap<String, Object>();
            String jwt = JwtService.generateToken(payload, "" );
            output.put("token", jwt);
            output.put("result", subscriptionMap);

            return ResponseEntity.ok(output);

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Crée un abonnement consommation par l'administrateur backoffice
     *
     * @param requestData Données de la requête avec les informations d'abonnement
     * @return L'objet subscription créé dans Stripe
     * @throws Exception Si l'administrateur n'est pas authentifié ou si la création échoue
     */
    @PostMapping("/boconsocreationabonnement")
    public Map<String, Object> createBackOfficeConsommationSubscription(@RequestBody Map<String, Object> requestData, @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (!"boconsocreationabonnement".equals(requestData.get("action"))) {
            throw new Exception("Action invalide");
        }

        if (payload.get("bo_auth").toString() == null || !"oui".equals(payload.get("bo_auth").toString())) {
            throw new Exception("Non authentifié");
        }

        String stripeCustomerId = (String) requestData.get("customerId");
        String priceId = (String) requestData.get("priceId");
        String paymentMethodId = (String) requestData.get("paymentMethodId");

        // Vérifier si le client existe dans la base de données
        Integer clientId = jdbcTemplate.queryForObject(
                "SELECT cltid FROM client WHERE stripe_customer_id = ? AND actif = 1",
                new Object[]{stripeCustomerId},
                Integer.class
        );

        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            throw new Exception("Erreur ! Client non trouvé");
        }

        payload.put("bocreationabonnement_priceid", priceId);

        // Configurer Stripe
        Stripe.apiKey = stripeSecretKey;

        try {
            // Attacher la méthode de paiement au client
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            Map<String, Object> paymentMethodParams = new HashMap<>();
            paymentMethodParams.put("customer", stripeCustomerId);
            paymentMethod.attach(paymentMethodParams);

            // Définir la méthode de paiement par défaut
            Map<String, Object> customerParams = new HashMap<>();
            Map<String, Object> invoiceSettings = new HashMap<>();
            invoiceSettings.put("default_payment_method", paymentMethodId);
            customerParams.put("invoice_settings", invoiceSettings);
            Customer.retrieve(stripeCustomerId).update(customerParams);

            // Créer l'abonnement
            Map<String, Object> item = new HashMap<>();
            item.put("price", priceId);

            Map<String, Object> subscriptionParams = new HashMap<>();
            subscriptionParams.put("customer", stripeCustomerId);
            subscriptionParams.put("items", Collections.singletonList(item));
            subscriptionParams.put("expand", Collections.singletonList("latest_invoice.payment_intent"));

            Subscription subscription = Subscription.create(subscriptionParams);

            // Enregistrer l'abonnement dans la base de données
            Integer bouticId = Integer.parseInt(payload.get("bo_id").toString());
            String subscriptionId = subscription.getId();

            jdbcTemplate.update(
                    "INSERT INTO abonnement(cltid, creationboutic, bouticid, stripe_subscription_id, actif) VALUES (?, 0, ?, ?, 1)",
                    clientId, bouticId, subscriptionId
            );

            // Récupérer l'ID de l'abonnement inséré
            Integer aboId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

            // Mettre à jour les métadonnées de l'abonnement
            String referenceNumber = "ABOPB" + String.format("%010d", aboId);
            Map<String, Object> metadataParams = new HashMap<>();
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("pbabonumref", referenceNumber);
            metadataParams.put("metadata", metadata);
            subscription.update(metadataParams);

            payload.put("bocreationabonnement_stripe_subscription_id", subscription.getId());

            // Convertir l'objet Subscription en Map via Gson
            Gson gson = new Gson();
            String jsonString = gson.toJson(subscription);
            Map<String, Object> subscriptionMap = gson.fromJson(jsonString, Map.class);
            HashMap<String, Object> output = new HashMap<String, Object>();
            String jwt = JwtService.generateToken(payload, "" );
            output.put("token", jwt);
            output.put("result", subscriptionMap);


            return output;

        } catch (StripeException e) {
            throw new Exception(e.getMessage());
        }
    }

    @PostMapping("/boannulerabonnement")
    public Map<String, Object> cancelSubscription(@RequestBody Map<String, Object> requestData, @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (!"boannulerabonnement".equals(requestData.get("action"))) {
            throw new Exception("Action invalide");
        }

        // Vérifier l'authentification admin
        if (payload.get("bo_auth").toString() == null || !"oui".equals(payload.get("bo_auth").toString())) {
            throw new Exception("Non authentifié");
        }

        String subscriptionId = (String) requestData.get("subscriptionid");

        // Configurer Stripe
        Stripe.apiKey = stripeSecretKey;

        try {
            // Mettre à jour l'abonnement pour l'annuler à la fin de la période
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("cancel_at_period_end", true);

            Subscription subscription = Subscription.retrieve(subscriptionId);
            subscription = subscription.update(updateParams);

            // Récupérer l'ID de l'abonnement dans la base de données
            Integer aboId = jdbcTemplate.queryForObject(
                    "SELECT aboid FROM abonnement WHERE stripe_subscription_id = ?",
                    new Object[]{subscriptionId},
                    Integer.class
            );

            // Désactiver l'abonnement dans la base de données
            jdbcTemplate.update("UPDATE abonnement SET actif = 0 WHERE aboid = ?", aboId);

            // Convertir l'objet Subscription en Map via Gson
            Gson gson = new Gson();
            String jsonString = gson.toJson(subscription);
            Map<String, Object> subscriptionMap = gson.fromJson(jsonString, Map.class);

            Map<String, Object> output = new HashMap<>();
            output.put("subscription", subscriptionMap);

            return output;

        } catch (StripeException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Réactive un abonnement précédemment annulé
     *
     * @param requestData Données de la requête contenant l'ID de l'abonnement

     * @return Un objet contenant l'abonnement réactivé
     * @throws Exception Si l'administrateur n'est pas authentifié ou si la réactivation échoue
     */
    @PostMapping("/boactivationabonnement")
    public Map<String, Object> activateSubscription(@RequestBody Map<String, Object> requestData, @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        if (!"boactivationabonnement".equals(requestData.get("action"))) {
            throw new Exception("Action invalide");
        }

        // Vérifier l'authentification admin
        if (payload.get("bo_auth").toString() == null || !"oui".equals(payload.get("bo_auth").toString())) {
            throw new Exception("Non authentifié");
        }

        String subscriptionId = (String) requestData.get("subscriptionId");

        // Récupérer l'ID de l'abonnement dans la base de données
        Integer aboId = jdbcTemplate.queryForObject(
                "SELECT aboid FROM abonnement WHERE stripe_subscription_id = ?",
                new Object[]{subscriptionId},
                Integer.class
        );

        // Réactiver l'abonnement dans la base de données
        jdbcTemplate.update("UPDATE abonnement SET actif = 1 WHERE aboid = ?", aboId);

        // Configurer Stripe
        Stripe.apiKey = environment.getProperty("STRIPE_SECRET_KEY");

        try {
            // Récupérer l'abonnement depuis Stripe
            Subscription subscription = Subscription.retrieve(subscriptionId);

            // Convertir l'objet Subscription en Map via Gson
            Gson gson = new Gson();
            String jsonString = gson.toJson(subscription);
            Map<String, Object> subscriptionMap = gson.fromJson(jsonString, Map.class);

            Map<String, Object> output = new HashMap<>();
            output.put("subscription", subscriptionMap);

            return output;

        } catch (StripeException e) {
            throw new Exception(e.getMessage());
        }
    }

    @PostMapping("/check-subscription")
    public ResponseEntity<?> checkSubscription(@RequestBody Map<String, Object> input, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            Map<String, Object> response = new HashMap<>();

            // Configuration de Stripe
            Stripe.apiKey = stripeSecretKey;
            // For sample support and debugging (not required for production)
            Stripe.setAppInfo(
                    "pratic-boutic/subscription",
                    "0.0.2",
                    "https://praticboutic.fr"
            );

            // Récupérer l'ID client Stripe depuis l'input
            String stripeCustomerId = (String) input.get("stripecustomerid");

            // Paramètres pour la requête Stripe
            SubscriptionListParams params = SubscriptionListParams.builder()
                    .setCustomer(stripeCustomerId)
                    .setStatus(SubscriptionListParams.Status.ACTIVE)
                    .build();

            SubscriptionCollection subscriptions = com.stripe.model.Subscription.list(params);

            // Vérifie la boutique active dans la base de données
            Integer actif = jdbcTemplate.queryForObject(
                    "SELECT customer.actif FROM customer " +
                            "JOIN client ON client.cltid = customer.cltid " +
                            "WHERE client.stripe_customer_id = ?",
                    new Object[]{stripeCustomerId},
                    Integer.class
            );

            boolean hasStripeSub = !subscriptions.getData().isEmpty();
            boolean isBoutiqueActive = (actif != null && actif == 1);

            if (hasStripeSub && isBoutiqueActive) {
                response.put("result", "OK");
            } else {
                response.put("result", "KO");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
