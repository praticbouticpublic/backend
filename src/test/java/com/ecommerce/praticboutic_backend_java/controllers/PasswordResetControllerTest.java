package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.exceptions.ClientNotFoundException;
import com.ecommerce.praticboutic_backend_java.exceptions.TooManyRequestsException;
import com.ecommerce.praticboutic_backend_java.requests.EmailVerificationRequest;
import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import com.ecommerce.praticboutic_backend_java.services.MotDePasseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetControllerTest {

    private PasswordResetController controller;
    private MotDePasseService motDePasseService;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    @BeforeEach
    void setUp() {
        controller = new PasswordResetController();
        motDePasseService = mock(MotDePasseService.class); // ✅ pas de RETURNS_DEEP_STUBS
        httpRequest = mock(HttpServletRequest.class);
        httpResponse = mock(HttpServletResponse.class);

        inject(controller, "motDePasseService", motDePasseService);
    }

    @Test
    @DisplayName("resetPassword - OK -> 200 et {result:OK}")
    void resetPassword_ok() throws Exception {
        EmailVerificationRequest req = new EmailVerificationRequest();
        req.setEmail("user@example.com");

        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);

        when(motDePasseService.reinitialiserMotDePasse("user@example.com", "1.2.3.4"))
                .thenReturn(true);

        ResponseEntity<?> resp = controller.resetPassword(req, httpRequest, httpResponse);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(Map.of("result", "OK"), resp.getBody());
    }

    @Test
    @DisplayName("resetPassword - ClientNotFound -> 404 et message")
    void resetPassword_clientNotFound() throws Exception {
        EmailVerificationRequest req = new EmailVerificationRequest();
        req.setEmail("unknown@example.com");

        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        doThrow(new ClientNotFoundException("Courriel non-trouvé"))
                .when(motDePasseService).reinitialiserMotDePasse(anyString(), anyString());

        ResponseEntity<?> resp = controller.resetPassword(req, httpRequest, httpResponse);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        ErrorResponse body = (ErrorResponse) resp.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("Courriel non-trouvé"));
    }

    @Test
    @DisplayName("resetPassword - TooManyRequests -> 429 et message")
    void resetPassword_tooManyRequests() throws Exception {
        EmailVerificationRequest req = new EmailVerificationRequest();
        req.setEmail("user@example.com");

        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        doThrow(new TooManyRequestsException("rate limited"))
                .when(motDePasseService).reinitialiserMotDePasse(anyString(), anyString());

        ResponseEntity<?> resp = controller.resetPassword(req, httpRequest, httpResponse);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, resp.getStatusCode());
        ErrorResponse body = (ErrorResponse) resp.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("rate limited"));
    }

    @Test
    @DisplayName("resetPassword - Exception -> 500 et message générique")
    void resetPassword_genericException() throws Exception {
        EmailVerificationRequest req = new EmailVerificationRequest();
        req.setEmail("user@example.com");

        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        doThrow(new RuntimeException("boom"))
                .when(motDePasseService).reinitialiserMotDePasse(anyString(), anyString());

        ResponseEntity<?> resp = controller.resetPassword(req, httpRequest, httpResponse);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        ErrorResponse body = (ErrorResponse) resp.getBody();
        assertNotNull(body);
        assertTrue(body.getError().contains("Une erreur est survenue"));
    }

    @Test
    @DisplayName("resetPassword - X-Forwarded-For prime sur RemoteAddr")
    void resetPassword_forwardedHeader() throws Exception {
        EmailVerificationRequest req = new EmailVerificationRequest();
        req.setEmail("user@example.com");

        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn("9.9.9.9, 8.8.8.8");

        when(motDePasseService.reinitialiserMotDePasse(anyString(), anyString()))
                .thenReturn(true);

        ResponseEntity<?> resp = controller.resetPassword(req, httpRequest, httpResponse);

        assertNotNull(resp);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(Map.of("result", "OK"), resp.getBody());
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
