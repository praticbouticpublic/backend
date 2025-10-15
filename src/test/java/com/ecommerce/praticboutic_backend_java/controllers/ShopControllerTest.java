package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.configurations.DatabaseConfig;
import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.BouticRequest;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class ShopControllerTest {

    private ShopController controller;
    private JdbcTemplate jdbcTemplate;
    private DatabaseConfig dbConfig;

    @BeforeEach
    void setUp() {
        // Pas de redéclaration locale
        jdbcTemplate = mock(JdbcTemplate.class);
        dbConfig = mock(DatabaseConfig.class, Answers.RETURNS_DEEP_STUBS);

        controller = new ShopController(jdbcTemplate);
        inject(controller, "dbConfig", dbConfig);
    }


    @Test
    @DisplayName("register-boutic - 401 si email non vérifié")
    void registerBoutic_unauthorized_whenEmailNotVerified() {
        BouticRequest req = new BouticRequest();
        req.setAliasboutic("newshop");

        Map<String, Object> payload = new HashMap<>(); // pas de verify_email

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.checkAliasAvailability(req, "Bearer tok");

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Courriel non vérifié"));
        }
    }

    @Test
    @DisplayName("register-boutic - 400 si alias vide")
    void registerBoutic_badRequest_whenAliasEmpty() {
        BouticRequest req = new BouticRequest();
        req.setAliasboutic("");

        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.checkAliasAvailability(req, "Bearer tok");

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Identifiant vide"));
        }
    }

    @Test
    @DisplayName("register-boutic - 409 si alias déjà utilisé")
    void registerBoutic_conflict_whenAliasTaken() {
        BouticRequest req = new BouticRequest();
        req.setAliasboutic("newshop");
        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        when(jdbcTemplate.queryForObject(
                eq("SELECT count(*) FROM customer cu WHERE cu.customer = ? LIMIT 1"),
                eq(Integer.class),
                eq("newshop"))).thenReturn(1);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.checkAliasAvailability(req, "Bearer tok");

            assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("déjà utilisé"));
        }
    }

    @Test
    @DisplayName("register-boutic - 400 si alias interdit")
    void registerBoutic_badRequest_whenAliasForbidden() {
        BouticRequest req = new BouticRequest();
        req.setAliasboutic("admin"); // interdit
        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.checkAliasAvailability(req, "Bearer tok");

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Identifiant interdit"));
        }
    }

    @Test
    @DisplayName("register-boutic - 200 et token quand alias disponible")
    void registerBoutic_ok_whenAliasAvailable() {
        BouticRequest req = new BouticRequest();
        req.setAliasboutic("newshop");
        req.setNom("Nom");
        req.setLogo("logo.png");
        req.setEmail("mail@ex.com");

        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        when(jdbcTemplate.queryForObject(
                eq("SELECT count(*) FROM customer cu WHERE cu.customer = ? LIMIT 1"),
                eq(Integer.class),
                eq("newshop"))).thenReturn(0);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt");

            ResponseEntity<?> resp = controller.checkAliasAvailability(req, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("OK", body.get("result"));
            assertEquals("new.jwt", body.get("token"));
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