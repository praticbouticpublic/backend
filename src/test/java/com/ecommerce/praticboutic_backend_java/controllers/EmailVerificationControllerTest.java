package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.EmailVerificationRequest;
import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmailVerificationControllerTest {

    private EmailVerificationController controller;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        controller = new EmailVerificationController();
        jdbcTemplate = mock(JdbcTemplate.class);
        inject(controller, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void verifyEmail_emailNotExist_returnsOk() throws Exception {
        EmailVerificationRequest request = new EmailVerificationRequest();
        request.setEmail("newuser@example.com");

        // DB retourne 0 → email n'existe pas
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString()))
                .thenReturn(0);

        // Créer un JwtPayload mocké
                Map<String, Object> claims = new HashMap<>();
                JwtPayload payload = mock(JwtPayload.class);
                when(payload.getClaims()).thenReturn(claims);

        // Static mock pour parseToken
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken(anyString()))
                    .thenReturn(payload); // ✅ doit renvoyer un objet concret

            ResponseEntity<?> resp = controller.verifyEmail(request, "Bearer faketoken");

            // Vérifications
            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("OK", body.get("result"));
            assertTrue(body.containsKey("token"));
        }

    }

    @Test
    void verifyEmail_emailExist_returnsKo() throws Exception {
        EmailVerificationRequest request = new EmailVerificationRequest();
        request.setEmail("existing@example.com");

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString()))
                .thenReturn(1); // email existe déjà

        Map<String, Object> claims = Map.of();
        JwtPayload payload = mock(JwtPayload.class);
        when(payload.getClaims()).thenReturn(claims);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken(anyString()))
                    .thenReturn(payload);

            ResponseEntity<?> resp = controller.verifyEmail(request, "Bearer faketoken");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("KO", body.get("result"));
            assertTrue(body.containsKey("token"));
        }
    }

    @Test
    void verifyEmail_dbException_returns500() throws Exception {
        EmailVerificationRequest request = new EmailVerificationRequest();
        request.setEmail("user@example.com");

        // Simuler exception DB
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyString()))
                .thenThrow(new RuntimeException("DB failure"));

        Map<String, Object> claims = Map.of();
        JwtPayload payload = mock(JwtPayload.class);
        when(payload.getClaims()).thenReturn(claims);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken(anyString()))
                    .thenReturn(payload);

            ResponseEntity<?> resp = controller.verifyEmail(request, "Bearer faketoken");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            ErrorResponse body = (ErrorResponse) resp.getBody();
            assertNotNull(body);
            assertTrue(body.getError().contains("DB failure"));
        }
    }

    // Méthode utilitaire pour injection
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
