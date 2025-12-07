package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.SuppressionRequest;
import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
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
    public ResponseEntity<?> supprimerCompte(@RequestBody SuppressionRequest request,
                                             HttpServletRequest servletRequest,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            JwtPayload payload = jwtService.parseToken(token);

            if (!jwtService.isAuthenticated(payload.getClaims())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Non authentifié"));
            }

            suppressionService.supprimerCompte(request, servletRequest.getRemoteAddr());
            return ResponseEntity.ok(Map.of("result", "OK"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

}
