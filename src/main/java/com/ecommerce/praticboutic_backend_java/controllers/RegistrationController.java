package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.RegistrationRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${session.max.lifetime}")
    private int sessionMaxLifetime;



    @PostMapping("/registration")
    public ResponseEntity<?> registerMobile(@RequestBody RegistrationRequest input,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
            // Vérification de l'email
            Object verifyEmail = payload.get("verify_email");
            if (verifyEmail == null || verifyEmail.toString().isEmpty()) {
                throw new Exception("Courriel non vérifié");
            }
            // Enregistrement des données dans la session
            payload.put("registration_pass", input.pass);
            payload.put("registration_qualite", input.qualite);
            payload.put("registration_nom", input.nom);
            payload.put("registration_prenom", input.prenom);
            payload.put("registration_adr1", input.adr1);
            payload.put("registration_adr2", input.adr2);
            payload.put("registration_cp", input.cp);
            payload.put("registration_ville", input.ville);
            payload.put("registration_tel", input.tel);
            // Configuration Stripe
            Stripe.apiKey = stripeSecretKey;
            // Création du client Stripe
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setAddress(
                            CustomerCreateParams.Address.builder()
                                    .setCity(input.ville)
                                    .setCountry("FRANCE")
                                    .setLine1(input.adr1)
                                    .setLine2(input.adr2)
                                    .setPostalCode(input.cp)
                                    .build()
                    )
                    .setEmail(verifyEmail.toString())
                    .setName(input.nom)
                    .setPhone(input.tel)
                    .build();

            Customer customer = Customer.create(params);
            payload.put("registration_stripe_customer_id", customer.getId());

            String jwt = JwtService.generateToken(payload, "" );
            Map<String, Object> response = new HashMap<>();
            response.put("result","OK");
            response.put("token", jwt);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
}