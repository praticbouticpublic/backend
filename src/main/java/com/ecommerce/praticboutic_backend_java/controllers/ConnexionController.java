package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.GoogleSignInRequest;
import com.ecommerce.praticboutic_backend_java.requests.LoginRequest;
import com.ecommerce.praticboutic_backend_java.responses.LoginResponse;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.SubscriptionCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConnexionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    @Value("${login.max.retry}")
    private int maxRetry;
    
    @Value("${login.retry.interval}")
    private String retryInterval;

    @Value("${google.client.id}")
    private String googleClientId;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(ConnexionController.class);


    @PostMapping("/authorize")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response;
        try {
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

            // Vérifier les tentatives de connexion
            String ip = httpRequest.getRemoteAddr();
            int attemptCount = (int) countLoginAttempts(ip);

            if (attemptCount >= maxRetry) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error",
                        "Vous êtes autorisé à " + maxRetry + " tentatives en " + retryInterval));
            }

            // Vérifier les identifiants
            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                    "SELECT c.pass, cu.customid, cu.customer, c.stripe_customer_id " +
                            "FROM client c, customer cu " +
                            "WHERE c.email = ? AND c.cltid = cu.cltid LIMIT 1",
                    request.getEmail());

            if (results.isEmpty()) {
                incrementLoginAttempts(ip);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Mauvais identifiant ou mot de passe !"));
            }

            Map<String, Object> userData = results.get(0);
            String hashedPassword = (String) userData.get("pass");

            // Vérifier le mot de passe
            if (!verifyPassword(request.getPassword(), hashedPassword)) {
                incrementLoginAttempts(ip);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Mauvais identifiant ou mot de passe !"));
            }

            // Créer ou mettre à jour la session
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("last_activity", System.currentTimeMillis() / 1000);
            sessionData.put("bo_stripe_customer_id", userData.get("stripe_customer_id"));
            sessionData.put("bo_id", userData.get("customid"));
            sessionData.put("bo_email", request.getEmail());
            sessionData.put("bo_auth", "oui");
            sessionData.put("bo_init", "non");
            sessionData.put("active", 1);

            // Mettre à jour la session ou en créer une nouvelle
            //sessionService.updateSession(sessionData);
            payload.putAll(sessionData);

            // Vérifier l'abonnement Stripe
            String stripecustomerid = (String) userData.get("stripe_customer_id");
            String subscriptionstatus = checkStripeSubscription(stripecustomerid);
            payload.put("bo_abo", subscriptionstatus.equals("OK") ? "oui" : "non");
            String jwt = JwtService.generateToken(payload, "" );

            // Créer la réponse
            response = new HashMap<>();
            response.put("token", jwt);
            response.put("bouticid", Integer.parseInt(userData.get("customid").toString()));
            response.put("customer", userData.get("customer"));
            response.put("stripecustomerid", stripecustomerid);
            response.put("subscriptionstatus", subscriptionstatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","error :" + e.getMessage()));
        }

        return ResponseEntity.ok(response);
    }
    
    private Object countLoginAttempts(String ip) {
        try {

            return jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM connexion WHERE ip = ? AND ts > (NOW() - INTERVAL " + retryInterval + ")",
                    Integer.class, ip);
        }
        catch(Exception e){
            logger.error("Exception : {}", e.getMessage());
            return -1;
        }
    }
    
    private void incrementLoginAttempts(String ip) {
        jdbcTemplate.update(
            "INSERT INTO connexion (ip, ts) VALUES (?, CURRENT_TIMESTAMP)",
            ip);
    }
    
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    private String checkStripeSubscription(String customerId) {
        try {
            Stripe.apiKey = stripeSecretKey;
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customerId);
            params.put("status", "active");
            
            SubscriptionCollection subscriptions = com.stripe.model.Subscription.list(params);
            
            return (!subscriptions.getData().isEmpty()) ? "OK" : "KO";
        } catch (StripeException e) {
            return "KO";
        }
    }

    private LoginResponse createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("status", status.toString());
        return (LoginResponse) response;
    }

    @PostMapping("/google-signin")
    public ResponseEntity<?> googleSignIn(@RequestBody GoogleSignInRequest request, HttpServletRequest httpRequest, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
            // Vérifier si l'utilisateur existe dans la base de données
            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                    "SELECT c.pass, cu.customid, cu.customer, c.stripe_customer_id " +
                            "FROM client c, customer cu " +
                            "WHERE c.email = ? AND c.cltid = cu.cltid LIMIT 1",
                    request.getEmail());

            // Si l'utilisateur n'existe pas, créer une réponse avec statut KO
            if (results.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("bouticid", "");
                response.put("customer", "");
                response.put("stripecustomerid", "");
                response.put("subscriptionstatus", "KO");
                response.put("status", "KO");
                response.put("password", "");
                // Enregistrer l'email vérifié pour un usage ultérieur
                //sessionService.setAttribute("verify_email", request.getEmail());
                payload.put("verify_email", request.getEmail());
                return ResponseEntity.ok(response);
            }

            // Récupérer les informations de l'utilisateur
            Map<String, Object> userData = results.get(0);

            // Créer ou mettre à jour la session
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("last_activity", System.currentTimeMillis() / 1000);
            sessionData.put("bo_stripe_customer_id", userData.get("stripe_customer_id"));
            sessionData.put("bo_id", userData.get("customid"));
            sessionData.put("bo_email", request.getEmail());
            sessionData.put("bo_auth", "oui");
            sessionData.put("bo_init", "non");
            sessionData.put("active", 1);

            // Mettre à jour la session
            //sessionService.updateSession(sessionData);
            payload.putAll(sessionData);

            // Vérifier l'abonnement Stripe
            String stripeCustomerId = (String) userData.get("stripe_customer_id");
            String subscriptionStatus = checkStripeSubscription(stripeCustomerId);
            sessionData.put("bo_abo", subscriptionStatus.equals("OK") ? "oui" : "non");
            String jwt = JwtService.generateToken(payload, "" );

            // Créer la réponse au format attendu par le front-end
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("bouticid", userData.get("customid"));
            response.put("customer", userData.get("customer"));
            response.put("stripecustomerid", stripeCustomerId);
            response.put("subscriptionstatus", subscriptionStatus);
            response.put("status", "OK");
            response.put("password", userData.get("pass"));  // Attention: à ne pas envoyer en production

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","error :" + e.getMessage()));
        }

    }

    private GoogleIdToken verifyGoogleToken(String idTokenString) throws IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        try {
            return verifier.verify(idTokenString);
        } catch (Exception e) {
            return null;
        }
    }



}