package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.SessionRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CheckVersionController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${app.authorization.file.path}")
    private String authorizationFilePath;
    
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/check")
    public ResponseEntity<?> checkVersion(@RequestBody SessionRequest request) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + authorizationFilePath);
            if (!resource.exists()) {
                throw new Exception("Fichier d'autorisation non trouvé");
            }

            try (InputStream is = resource.getInputStream()) {
                JsonNode authorizationData = objectMapper.readTree(is);
                return ResponseEntity.ok(Map.of("result", authorizationData));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Alternative utilisant ResourceLoader si le fichier est dans les ressources de l'application
    @PostMapping("/check-alt")
    public ResponseEntity<?> checkVersionAlt(@RequestBody SessionRequest request) {
        try {

            // Lire le fichier d'autorisation depuis les ressources
            Resource resource = resourceLoader.getResource("classpath:mobileapp/authorisation.json");
            
            if (!resource.exists()) {
                throw new Exception("Fichier d'autorisation non trouvé");
            }
            
            // Lire le contenu JSON du fichier
            try (InputStream inputStream = resource.getInputStream()) {
                JsonNode authorizationData = objectMapper.readTree(inputStream);
                return ResponseEntity.ok(Map.of("result", authorizationData));
            }
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