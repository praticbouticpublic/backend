package com.ecommerce.praticboutic_backend_java.controllers;



import com.ecommerce.praticboutic_backend_java.requests.ShopConfigRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShopSettingsController {

    @PostMapping("/boutic-configure")
    public ResponseEntity<?> configureShop(@RequestBody ShopConfigRequest request, @RequestHeader("Authorization") String authHeader) {
        try {

            String token = authHeader.replace("Bearer ", "");

            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
            // Vérifier si l'email est vérifié
            Object verifyEmail = payload.get("verify_email");
            if (verifyEmail == null || verifyEmail.toString().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Courriel non vérifié"));
            }
            // Enregistrer les configurations dans la session
            payload.put("confboutic_chxmethode", request.getChxmethode());
            payload.put("confboutic_chxpaie", request.getChxpaie());
            payload.put("confboutic_mntmincmd", request.getMntmincmd());
            payload.put("confboutic_validsms", request.getValidsms());
            String jwt = JwtService.generateToken(payload, "" );

            Map<String, Object> response = new HashMap<>();
            response.put("result", "OK");
            response.put("token", jwt);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","Erreur: " + e.getMessage()));
        }
    }
}