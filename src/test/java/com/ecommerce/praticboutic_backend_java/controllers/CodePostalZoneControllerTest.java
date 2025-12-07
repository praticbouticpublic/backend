package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.CpZoneRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodePostalZoneControllerTest {

    private CodePostalZoneController controller;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        controller = new CodePostalZoneController();
        inject(controller, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    @DisplayName("Code postal présent - retourne ok")
    void checkCpZone_present_returnsOk() {
        CpZoneRequest request = new CpZoneRequest();
        request.setCustomer("myshop");
        request.setCp("75001");

        // Mock du token JWT
        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "myshop");
        payload.put("method", "POST");
        payload.put("table", "cpzone");
        payload.put("myshop_mail", "non");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("token123"))
                    .thenReturn(new JwtPayload(null, null, payload));

            // Mock JdbcTemplate pour customid
            when(jdbcTemplate.queryForObject(
                    eq("SELECT customid FROM customer WHERE customer = ?"),
                    eq(Integer.class),
                    eq("myshop")
            )).thenReturn(1);

            // Mock JdbcTemplate pour cpzone
            when(jdbcTemplate.queryForObject(
                    eq("SELECT COUNT(*) FROM cpzone WHERE customid = ? AND codepostal = ? AND actif = 1"),
                    eq(Integer.class),
                    eq(1),
                    eq("75001")
            )).thenReturn(1);

            ResponseEntity<?> resp = controller.checkCpZone(request, "Bearer token123");
            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("ok", body.get("result"));
        }
    }

    @Test
    @DisplayName("Code postal absent - retourne ko")
    void checkCpZone_absent_returnsKo() {
        CpZoneRequest request = new CpZoneRequest();
        request.setCustomer("myshop");
        request.setCp("99999");

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "myshop");
        payload.put("method", "POST");
        payload.put("table", "cpzone");
        payload.put("myshop_mail", "non");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("token123"))
                    .thenReturn(new JwtPayload(null, null, payload));

            when(jdbcTemplate.queryForObject(
                    anyString(), eq(Integer.class), eq("myshop")
            )).thenReturn(1);

            when(jdbcTemplate.queryForObject(
                    anyString(), eq(Integer.class), eq(1), eq("99999")
            )).thenReturn(0);

            ResponseEntity<?> resp = controller.checkCpZone(request, "Bearer token123");
            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("ko", body.get("result"));
        }
    }

    @Test
    @DisplayName("Erreur - token sans customer")
    void checkCpZone_missingCustomer_throwsError() {
        CpZoneRequest request = new CpZoneRequest();
        request.setCustomer("shop");
        request.setCp("75001");

        Map<String, Object> payload = new HashMap<>();
        payload.put("method", "POST");
        payload.put("table", "cpzone");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("token123"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.checkCpZone(request, "Bearer token123");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertTrue(body.get("error").toString().contains("Pas de boutic"));
        }
    }

    private static void inject(Object target, String field, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("Injection échouée pour le champ " + field + ": " + e.getMessage());
        }
    }
}
