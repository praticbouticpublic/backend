package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.services.JwtService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class LogoutControllerTest {

    private LogoutController controller;

    @BeforeEach
    void setUp() {
        controller = new LogoutController();
    }

    @Test
    @DisplayName("logout - nettoie la session et retourne 200 avec nouveau token")
    void logout_ok() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bo_auth", "oui");
        payload.put("bo_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, payload));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt");

            HttpSession session = mock(HttpSession.class);
            ResponseEntity<?> resp = controller.logout(session, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertTrue(body.containsKey("token"));
            assertEquals("new.jwt", body.get("token"));
        }
    }
}
