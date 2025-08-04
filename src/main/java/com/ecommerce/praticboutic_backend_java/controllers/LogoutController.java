package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession currentSession, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
            // Réinitialisation des attributs de session (équivalent à ce qui est fait dans le PHP)
            //sessionService.setAttribute("active", 0);
            payload.put("active", 0);
            //sessionService.setAttribute("last_activity", Instant.now().getEpochSecond());
            payload.put("last_activity", Instant.now().getEpochSecond());
            //sessionService.setAttribute("bo_stripe_customer_id", "");
            payload.put("bo_stripe_customer_id", "");
            //sessionService.setAttribute("bo_id", 0);
            payload.put("bo_id", 0);
            //sessionService.setAttribute("bo_email", "");
            payload.put("bo_email", "");
            //sessionService.setAttribute("bo_auth", "non");
            payload.put("bo_auth", "non");
            //sessionService.setAttribute("bo_init", "oui");
            payload.put("bo_init", "oui");

            // Option alternative: invalider complètement la session
            // currentSession.invalidate();
            String jwt = JwtService.generateToken(payload, "" );

            Map<String, String> response = new HashMap<>();
            response.put("status", "OK");
            response.put("token", jwt);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}