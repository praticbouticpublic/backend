package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.ChargeRequest;
import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.ParameterService;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import com.stripe.Stripe;
import com.stripe.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChargeController {

    @Autowired
    private ParameterService parameterService;

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @Value("${session.max.lifetime}")
    private Long sessionMaxLifetime;

    @Autowired
    protected JwtService jwtService;

    @PostMapping("/check-stripe-account")
    public ResponseEntity<?> checkStripeAccount(@RequestBody ChargeRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

            // Vérifier l'authentification
            if (!jwtService.isAuthenticated(payload)) {
                throw new Exception("Non authentifié");
            }

            // Récupérer l'ID de boutique
            Integer bouticId = request.getBouticid();
            if (bouticId == null) {
                throw new Exception("ID de boutique manquant");
            }

            // Récupérer l'ID du compte Stripe
            String stripeAccountId = parameterService.getParameterValue("STRIPE_ACCOUNT_ID", bouticId);
            if (stripeAccountId == null || stripeAccountId.isEmpty()) {
                return ResponseEntity.ok(Map.of("result","KO"));
            }

            // Configurer Stripe
            Stripe.apiKey = stripeApiKey;

            // Vérifier si le compte Stripe est activé pour les paiements
            Account account = Account.retrieve(stripeAccountId);
            String result = account.getChargesEnabled() ? "OK" : "KO";

            return ResponseEntity.ok(Map.of("result", result));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}