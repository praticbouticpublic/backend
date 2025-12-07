package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

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
            Object customerObj = payload.get("customer");
            if (customerObj == null || customerObj.toString().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Pas de boutic"));
            }
            String customer = customerObj.toString();

            // Vérifier si l'email est défini
            Object mailObj = payload.get(customer + "_mail");
            if (mailObj == null || mailObj.toString().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Pas de courriel"));
            }
            String mail = mailObj.toString();

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
            String msg = e.getMessage() != null ? e.getMessage() : "Erreur interne";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(msg));
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

        public String getCustomer() { return customer; }
        public void setCustomer(String customer) { this.customer = customer; }
        public double getSstotal() { return sstotal; }
        public void setSstotal(double sstotal) { this.sstotal = sstotal; }
    }

    public static class ShippingCostResponse {
        private final double cost;
        public ShippingCostResponse(double cost) { this.cost = cost; }
        public double getCost() { return cost; }
    }

    public static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }
}
