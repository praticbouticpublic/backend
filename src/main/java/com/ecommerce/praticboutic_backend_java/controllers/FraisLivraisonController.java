package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.entities.*;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;

@RestController
@RequestMapping("/api")
public class FraisLivraisonController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/frais-livr")
    public ResponseEntity<?> getFraisLivr(@RequestBody ShippingCostRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            Map<String, Object> payload = JwtService.parseToken(token).getClaims();

            // Vérifier si le client existe dans la session
            String customer = payload.get("customer").toString();
            if (customer == null || customer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Pas de boutic"));
            }

            // Vérifier si l'email est défini
            String mail = payload.get(customer + "_mail").toString();
            if (mail == null || mail.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Pas de courriel"));
            }

            if ("oui".equals(mail)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Courriel déjà envoyé"));
            }

            // Récupérer les valeurs de la requête
            double val = request.getSstotal();
            String customerName = request.getCustomer();
            int customerId = getCustomerId(customerName);
            
            // Calculer les frais de livraison
            double surcout = calculateShippingCost(customerId, val);
            
            return ResponseEntity.ok(new ShippingCostResponse(surcout));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    private int getCustomerId(String customer) throws SQLException {
        int customid = 0;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT customid FROM customer WHERE customer = ?")) {
            stmt.setString(1, customer);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    customid = rs.getInt("customid");
                }
            }
        }
        return customid;
    }
    
    private double calculateShippingCost(int customid, double val) throws SQLException {
        double surcout = 0;
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT surcout FROM barlivr WHERE customid = ? AND valminin <= ? " +
                           "AND (valmaxex > ? OR valminin >= valmaxex) AND actif = 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, customid);
                stmt.setDouble(2, val);
                stmt.setDouble(3, val);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        surcout = rs.getDouble("surcout");
                    }
                }
            }
        }
        return surcout;
    }
    
    // Classes pour la sérialisation/désérialisation JSON
    public static class ShippingCostRequest {
        private String customer;
        private double sstotal;

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public double getSstotal() {
            return sstotal;
        }

        public void setSstotal(double sstotal) {
            this.sstotal = sstotal;
        }
    }
    
    public static class ShippingCostResponse {
        private final double cost;

        public ShippingCostResponse(double cost) {
            this.cost = cost;
        }

        public double getCost() {
            return cost;
        }
    }
    
    public static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}