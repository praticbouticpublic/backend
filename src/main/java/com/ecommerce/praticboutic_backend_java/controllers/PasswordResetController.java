package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.exceptions.ClientNotFoundException;
import com.ecommerce.praticboutic_backend_java.exceptions.TooManyRequestsException;
import com.ecommerce.praticboutic_backend_java.requests.EmailVerificationRequest;
import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import com.ecommerce.praticboutic_backend_java.services.MotDePasseService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    @Autowired
    private MotDePasseService motDePasseService;
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody EmailVerificationRequest request, 
                                          HttpServletRequest httpRequest, HttpServletResponse httpResponse
    ) {
        try {

            // Obtenir l'adresse IP du client
            String ipAddress = httpRequest.getRemoteAddr();
            // X-Forwarded-For header pour les clients derrière un proxy
            String forwardedIp = httpRequest.getHeader("X-Forwarded-For");
            if (forwardedIp != null && !forwardedIp.isEmpty()) {
                ipAddress = forwardedIp.split(",")[0].trim();
            }
            
            // Réinitialiser le mot de passe
            motDePasseService.reinitialiserMotDePasse(request.getEmail(), ipAddress);
            
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("result","OK"));
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Courriel non-trouvé"));
        } catch (TooManyRequestsException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Une erreur est survenue: " + e.getMessage()));
        }
    }
    

}