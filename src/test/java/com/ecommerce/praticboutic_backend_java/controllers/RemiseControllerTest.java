package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.RemiseRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class RemiseControllerTest {

    private RemiseController controller;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        controller = new RemiseController();
        jdbcTemplate = mock(JdbcTemplate.class, Answers.RETURNS_DEEP_STUBS);
        inject(controller, "jdbcTemplate", jdbcTemplate);
        inject(controller, "sessionMaxLifetime", 3600);
    }

    @Test
    @DisplayName("calculateRemise - happy path -> 200 et bon calcul")
    void calculateRemise_happyPath() {
        RemiseRequest req = new RemiseRequest();
        req.customer = "cust-01";
        req.code = "PROMO10";
        req.sstotal = 100.0;

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust");
        payload.put("cust_mail", "non");

        when(jdbcTemplate.queryForObject(
                eq("SELECT customid FROM customer WHERE customer = ?"),
                eq(Integer.class),
                eq("cust01")
        )).thenReturn(42);

        when(jdbcTemplate.query(
                eq("SELECT taux FROM promotion WHERE customid = ? AND BINARY code = ? AND actif = 1"),
                any(org.springframework.jdbc.core.RowMapper.class),
                eq(42),
                eq("PROMO10")
        )).thenAnswer(inv -> List.of(10.0));

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.calculateRemise(req, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertEquals(10.0, ((Map<?, ?>) resp.getBody()).get("result"));
        }
    }

    @Test
    @DisplayName("calculateRemise - pas de boutic -> 500")
    void calculateRemise_noCustomer() {
        RemiseRequest req = new RemiseRequest();
        Map<String, Object> payload = new HashMap<>();

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.calculateRemise(req, "Bearer tok");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Pas de boutic"));
        }
    }

    @Test
    @DisplayName("calculateRemise - pas de courriel -> 500")
    void calculateRemise_noEmail() {
        RemiseRequest req = new RemiseRequest();
        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.calculateRemise(req, "Bearer tok");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Pas de courriel"));
        }
    }

    @Test
    @DisplayName("calculateRemise - courriel déjà envoyé -> 500")
    void calculateRemise_emailAlreadySent() {
        RemiseRequest req = new RemiseRequest();
        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust");
        payload.put("cust_mail", "oui");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.calculateRemise(req, "Bearer tok");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Courriel déjà envoyé"));
        }
    }

    @Test
    @DisplayName("calculateRemise - customer inconnu -> 500")
    void calculateRemise_unknownCustomer() {
        RemiseRequest req = new RemiseRequest();
        req.customer = "cust-01";
        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust");
        payload.put("cust_mail", "non");

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), any()))
                .thenReturn(null);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.calculateRemise(req, "Bearer tok");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Customer non trouvé"));
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