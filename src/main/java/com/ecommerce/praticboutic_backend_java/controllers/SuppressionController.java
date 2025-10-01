package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.SuppressionRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import com.ecommerce.praticboutic_backend_java.services.SuppressionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SuppressionController {

    @Autowired
    private SuppressionService suppressionService;

    @Autowired
    protected JwtService jwtService;

    @PostMapping("/suppression")
    public ResponseEntity<?> supprimerCompte(@RequestBody SuppressionRequest request, HttpServletRequest servletRequest, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
            if (!jwtService.isAuthenticated(payload)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Non authentifié"));
            }
            suppressionService.supprimerCompte(request, servletRequest.getRemoteAddr());
            return ResponseEntity.ok(Map.of("result", "OK"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
