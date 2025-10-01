package com.ecommerce.praticboutic_backend_java.controllers;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class SessionController {
    @Autowired
    protected JwtService jwtService;
	
    @PostMapping("/session-marche")
    public ResponseEntity<Map<String, Object>> handleSession(@RequestBody(required = false) Map<String, Object> input) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();

        try {
            response.put("token", JwtService.generateToken(payload, ""));
        } catch (Exception e) {
            // Gestion des erreurs
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }

        // Retourner une réponse JSON
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/active-session")
    public ResponseEntity<Map<String, Object>> handleActsess(@RequestBody(required = false) Map<String, Object> input, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        Map<String, Object> response = new HashMap<>();

        try {

            // Vérifie l'état "active" de la session et prépare la réponse
            Integer test = Integer.valueOf(payload.get("active").toString());
            if (payload.get("active") != null && test.equals(1)) {
                response.put("status", "OK");
                response.put("email", payload.get("bo_email"));
            } else {
                response.put("status", "KO");
                response.put("email", "");
            }

        } catch (Exception e) {
            // Retourne une erreur en cas de problème
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }

        // Retourne une réponse JSON avec les informations de la session
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exit")
    public ResponseEntity<Map<String, Object>> createSession(@RequestBody(required = false) Map<String, Object> input,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        Map<String, Object> response = new HashMap<>();
        try {

            // Initialisation des attributs de session
            payload.put("active", 0);
            payload.put("last_activity", System.currentTimeMillis() / 1000);
            payload.put("bo_stripe_customer_id", "");
            payload.put("bo_id", 0);
            payload.put("bo_email", "");
            payload.put("bo_auth", "non");
            payload.put("bo_init", "oui");

            return ResponseEntity.status(HttpStatus.OK).body(payload);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    

}
