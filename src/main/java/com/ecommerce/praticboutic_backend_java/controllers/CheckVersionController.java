package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.SessionRequest;
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
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CheckVersionController {

    private final ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${authorization.file.path:mobileapp/authorisation.json}")
    private String authorizationFilePath;

    public CheckVersionController(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/check-version")
    public ResponseEntity<?> checkVersion(@RequestBody SessionRequest request) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + authorizationFilePath);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            try (InputStream is = resource.getInputStream()) {
                JsonNode jsonNode = objectMapper.readTree(is);
                return ResponseEntity.ok(jsonNode); // ✅ direct, pas d'enveloppe
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/check-version-alt")
    public ResponseEntity<?> checkVersionAlt(@RequestBody SessionRequest request) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + authorizationFilePath);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            try (InputStream is = resource.getInputStream()) {
                JsonNode jsonNode = objectMapper.readTree(is);
                return ResponseEntity.ok(jsonNode); // ✅ direct aussi
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
