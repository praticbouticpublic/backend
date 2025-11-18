package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.controllers.FraisLivraisonController.ShippingCostRequest;
import com.ecommerce.praticboutic_backend_java.controllers.FraisLivraisonController.ShippingCostResponse;
import com.ecommerce.praticboutic_backend_java.controllers.FraisLivraisonController.ErrorResponse;
import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FraisLivraisonControllerTest {

    private FraisLivraisonController controller;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        controller = new FraisLivraisonController();
        dataSource = mock(DataSource.class);
        inject(controller, "dataSource", dataSource);
    }

    @Test
    @DisplayName("getFraisLivr - happy path retourne 200 et cost")
    void getFraisLivr_happyPath() throws Exception {
        ShippingCostRequest req = new ShippingCostRequest();
        req.setCustomer("cust");
        req.setSstotal(25.0);

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust");
        payload.put("cust_mail", "non");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            Connection conn = mock(Connection.class);
            PreparedStatement ps1 = mock(PreparedStatement.class);
            ResultSet rs1 = mock(ResultSet.class);
            when(dataSource.getConnection()).thenReturn(conn);
            when(conn.prepareStatement("SELECT customid FROM customer WHERE customer = ?")).thenReturn(ps1);
            when(ps1.executeQuery()).thenReturn(rs1);
            when(rs1.next()).thenReturn(true);
            when(rs1.getInt("customid")).thenReturn(42);

            PreparedStatement ps2 = mock(PreparedStatement.class);
            ResultSet rs2 = mock(ResultSet.class);
            when(conn.prepareStatement(startsWith("SELECT surcout FROM barlivr"))).thenReturn(ps2);
            when(ps2.executeQuery()).thenReturn(rs2);
            when(rs2.next()).thenReturn(true);
            when(rs2.getDouble("surcout")).thenReturn(3.5);

            ResponseEntity<?> resp = controller.getFraisLivr(req, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            ShippingCostResponse body = (ShippingCostResponse) resp.getBody();
            assertNotNull(body);
            assertEquals(3.5, body.getCost());
        }
    }

    @Test
    @DisplayName("getFraisLivr - 500 si pas de boutic dans session")
    void getFraisLivr_noCustomerInSession() {
        ShippingCostRequest req = new ShippingCostRequest();
        req.setCustomer("cust");
        req.setSstotal(25.0);

        Map<String, Object> payload = new HashMap<>();

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.getFraisLivr(req, "Bearer tok");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            ErrorResponse body = (ErrorResponse) resp.getBody();
            assertNotNull(body);
            assertTrue(body.getError().contains("Pas de boutic"));
        }
    }

    @Test
    @DisplayName("getFraisLivr - 500 si pas de courriel dans session")
    void getFraisLivr_noEmailInSession() {
        ShippingCostRequest req = new ShippingCostRequest();
        req.setCustomer("cust");
        req.setSstotal(25.0);

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.getFraisLivr(req, "Bearer tok");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            ErrorResponse body = (ErrorResponse) resp.getBody();
            assertNotNull(body);
            assertTrue(body.getError().contains("Pas de courriel"));
        }
    }

    @Test
    @DisplayName("getFraisLivr - 500 si courriel déjà envoyé")
    void getFraisLivr_emailAlreadySent() {
        ShippingCostRequest req = new ShippingCostRequest();
        req.setCustomer("cust");
        req.setSstotal(25.0);

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust");
        payload.put("cust_mail", "oui");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.getFraisLivr(req, "Bearer tok");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            ErrorResponse body = (ErrorResponse) resp.getBody();
            assertNotNull(body);
            assertTrue(body.getError().contains("Courriel déjà envoyé"));
        }
    }

    @Test
    @DisplayName("getFraisLivr - 500 si SQL exception")
    void getFraisLivr_sqlException() throws Exception {
        ShippingCostRequest req = new ShippingCostRequest();
        req.setCustomer("cust");
        req.setSstotal(25.0);

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust");
        payload.put("cust_mail", "non");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            when(dataSource.getConnection()).thenThrow(new SQLException("db down"));

            ResponseEntity<?> resp = controller.getFraisLivr(req, "Bearer tok");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            ErrorResponse body = (ErrorResponse) resp.getBody();
            assertNotNull(body);
            assertTrue(body.getError().contains("db down"));
        }
    }

    private static void inject(Object target, String field, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("Injection échouée: " + field + " - " + e.getMessage());
        }
    }
}
