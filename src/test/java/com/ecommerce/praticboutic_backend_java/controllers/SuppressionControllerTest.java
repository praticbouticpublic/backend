package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.SuppressionRequest;
import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.SuppressionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SuppressionControllerTest {

    private SuppressionController controller;
    private SuppressionService suppressionService;
    private JwtService jwtService;
    private HttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        controller = new SuppressionController();

        suppressionService = mock(SuppressionService.class);
        jwtService = mock(JwtService.class);
        servletRequest = mock(HttpServletRequest.class);

        inject(controller, "suppressionService", suppressionService);
        inject(controller, "jwtService", jwtService);
    }

    @Test
    @DisplayName("supprimerCompte - OK renvoie 200")
    void supprimerCompte_ok() throws Exception {
        SuppressionRequest request = new SuppressionRequest();
        request.setEmail("user@example.com");
        request.setBouticid(42);

        when(servletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("bo_auth", "oui");
        JwtPayload payload = new JwtPayload(null, null, payloadMap);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken(anyString())).thenReturn(payload);
            when(jwtService.isAuthenticated(payloadMap)).thenReturn(true);

            ResponseEntity<?> resp = controller.supprimerCompte(request, servletRequest, "Bearer faketoken");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("OK", body.get("result"));

            verify(suppressionService).supprimerCompte(request, "127.0.0.1");
        }
    }

    @Test
    @DisplayName("supprimerCompte - non authentifié renvoie 401")
    void supprimerCompte_unauthenticated() throws Exception {
        SuppressionRequest request = new SuppressionRequest();
        request.setEmail("user@example.com");
        request.setBouticid(42);

        when(servletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, Object> payloadMap = new HashMap<>(); // payload vide → non authentifié
        JwtPayload payload = new JwtPayload(null, null, payloadMap);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken(anyString())).thenReturn(payload);
            when(jwtService.isAuthenticated(payloadMap)).thenReturn(false);

            ResponseEntity<?> resp = controller.supprimerCompte(request, servletRequest, "Bearer faketoken");

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());

            ErrorResponse body = (ErrorResponse) resp.getBody(); // <- ici
            assertEquals("Non authentifié", body.getError());

            verify(suppressionService, never()).supprimerCompte(any(), anyString());
        }
    }


    @Test
    @DisplayName("supprimerCompte - exception renvoie 500")
    void supprimerCompte_exception() throws Exception {
        SuppressionRequest request = new SuppressionRequest();
        request.setEmail("user@example.com");
        request.setBouticid(42);

        when(servletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("bo_auth", "oui");
        JwtPayload payload = new JwtPayload(null, null, payloadMap);

        // Faire lever une exception depuis le service
        doThrow(new RuntimeException("Boom")).when(suppressionService).supprimerCompte(any(), anyString());

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken(anyString())).thenReturn(payload);
            when(jwtService.isAuthenticated(payloadMap)).thenReturn(true);

            ResponseEntity<?> resp = controller.supprimerCompte(request, servletRequest, "Bearer faketoken");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());

            ErrorResponse body = (ErrorResponse) resp.getBody(); // <- correct
            assertTrue(body.getError().contains("Boom"));

            verify(suppressionService).supprimerCompte(request, "127.0.0.1");
        }
    }


    // Méthode utilitaire pour injection de dépendances
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
