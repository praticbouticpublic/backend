package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.configurations.DatabaseConfig;
import com.ecommerce.praticboutic_backend_java.requests.BouticRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShopController {

    @Autowired
    private DatabaseConfig dbConfig;


    private final JdbcTemplate jdbcTemplate;

    private final List<String> FORBIDDEN_IDS = Arrays.asList("admin", "common", "route", "upload", "vendor");

    public ShopController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @PostMapping("/register-boutic")
    public ResponseEntity<?> checkAliasAvailability(@RequestBody BouticRequest request,
                                                    @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
            // Vérifier si l'email est vérifié
            Object verifyEmail = payload.get("verify_email");
            if (verifyEmail == null || verifyEmail.toString().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Courriel non vérifié"));
            }
            // Valider l'alias
            if (request.getAliasboutic() == null || request.getAliasboutic().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Identifiant vide"));
            }
            if (FORBIDDEN_IDS.contains(request.getAliasboutic())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Identifiant interdit"));
            }
            // Vérifier si l'alias est déjà utilisé
            String sql = "SELECT count(*) FROM customer cu WHERE cu.customer = ? LIMIT 1";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, request.getAliasboutic());
            if (count != null && count > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","Alias de boutic déjà utilisé"));
            }
            // Enregistrer les informations dans la session
            payload.put("initboutic_aliasboutic", request.getAliasboutic());
            payload.put("initboutic_nom", request.getNom());
            payload.put("initboutic_logo", request.getLogo());
            payload.put("initboutic_email", request.getEmail());
            String jwt = JwtService.generateToken(payload, "" );
            Map<String, Object> response = new HashMap<>();
            response.put("result", "OK");
            response.put("token", jwt);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur: " + e.getMessage());
        }
    }
}