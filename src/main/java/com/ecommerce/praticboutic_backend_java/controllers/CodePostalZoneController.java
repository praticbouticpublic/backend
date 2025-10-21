package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.CpZoneRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CodePostalZoneController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${session.max.lifetime}")
    private Long sessionMaxLifetime;

    @PostMapping("/check-codepostal")
    public ResponseEntity<?> checkCpZone(@RequestBody CpZoneRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

            // Vérifier si customer est défini dans la session
            if (!payload.containsKey("customer")) {
                throw new Exception("Pas de boutic");
            }

            String customer = payload.get("customer").toString();
            String method = payload.get("method").toString();
            String table = payload.get("table").toString();

            // Vérifier si le courriel est défini
            String mailKey = customer + "_mail";
            if (!payload.containsKey(mailKey)) {
                throw new Exception("Pas de courriel");
            }

            // Vérifier si le courriel a déjà été envoyé
            if ("oui".equals(payload.get(mailKey).toString())) {
                throw new Exception("Courriel déjà envoyé");
            }

            // Récupérer customer de la requête et échapper les caractères spéciaux
            String requestCustomer = request.getCustomer();
            Integer customid;
            try
            {
                // Obtenir l'ID du client
                customid = jdbcTemplate.queryForObject(
                    "SELECT customid FROM customer WHERE customer = ?",
                    Integer.class,
                    requestCustomer
                );
            }
            catch (EmptyResultDataAccessException e) {
                customid = null;
            }

            if (customid == null) {
                return ResponseEntity.ok(Map.of("result","ko"));
            }

            // Vérifier si le code postal est dans une zone active pour ce client
            String cp = request.getCp();
            Integer count;
            try {
                count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM cpzone WHERE customid = ? AND codepostal = ? AND actif = 1",
                        Integer.class,
                        customid,
                        cp
                );
            }
            catch (EmptyResultDataAccessException e) {
                    count = 0;
            }

            String result = (count != null && count > 0) ? "ok" : "ko";

            return ResponseEntity.ok(Map.of("result", result));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Classe pour représenter les réponses d'erreur
    private static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}