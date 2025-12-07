package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class SessionControllerTest {

    @Test
    @DisplayName("handleSession - retourne un token")
    void handleSession_returnsToken() {
        SessionController controller = new SessionController();

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.generateToken(Mockito.anyMap(), Mockito.anyString()))
                    .thenReturn("new.jwt");

            ResponseEntity<Map<String, Object>> resp = controller.handleSession(null);

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertEquals("new.jwt", resp.getBody().get("token"));
        }
    }

    @Test
    @DisplayName("handleActsess - active=1 -> OK et email")
    void handleActsess_activeOk() {
        SessionController controller = new SessionController();

        Map<String, Object> payload = new HashMap<>();
        payload.put("active", 1);
        payload.put("bo_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<Map<String, Object>> resp = controller.handleActsess(null, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertEquals("OK", resp.getBody().get("status"));
            assertEquals("user@example.com", resp.getBody().get("email"));
        }
    }

    @Test
    @DisplayName("handleActsess - actif != 1 -> KO et email vide")
    void handleActsess_inactive() {
        SessionController controller = new SessionController();

        Map<String, Object> payload = new HashMap<>();
        payload.put("active", 0);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<Map<String, Object>> resp = controller.handleActsess(null, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertEquals("KO", resp.getBody().get("status"));
            assertEquals("", resp.getBody().get("email"));
        }
    }

    @Test
    @DisplayName("createSession (/exit) - remet la session à zéro")
    void createSession_exit_resets() {
        SessionController controller = new SessionController();

        Map<String, Object> payload = new HashMap<>();
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<Map<String, Object>> resp = controller.createSession(null, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<String,Object> body = resp.getBody();
            assertNotNull(body);
            assertEquals(0, body.get("active"));
            assertEquals("", body.get("bo_stripe_customer_id"));
            assertEquals(0, body.get("bo_id"));
            assertEquals("", body.get("bo_email"));
            assertEquals("non", body.get("bo_auth"));
            assertEquals("oui", body.get("bo_init"));
            assertTrue(body.containsKey("last_activity"));
        }
    }
}