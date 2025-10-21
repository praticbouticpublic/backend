package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.GoogleSignInRequest;
import com.ecommerce.praticboutic_backend_java.requests.LoginRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.stripe.exception.ApiException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import jakarta.servlet.http.HttpServletRequest;
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
import static org.mockito.Mockito.*;

// ... existing code ...

class ConnexionControllerTest {

    private ConnexionController controller;

    private JdbcTemplate jdbcTemplate;
    private HttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class, Answers.RETURNS_DEEP_STUBS);
        httpRequest = mock(HttpServletRequest.class, Answers.RETURNS_DEEP_STUBS);

        controller = new ConnexionController();

        set(controller, "jdbcTemplate", jdbcTemplate);
        set(controller, "maxRetry", 5);
        set(controller, "retryInterval", "10 MINUTE");
        set(controller, "googleClientId", "google-client-id");
    }

    @Test
    @DisplayName("login - succès: renvoie token et infos utilisateur")
    void login_success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("plain");
        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        when(jdbcTemplate.queryForObject(
                contains("SELECT COUNT(*) FROM connexion"),
                eq(Integer.class),
                any()))
                .thenReturn(0);

        Map<String, Object> row = new HashMap<>();
        row.put("pass", org.springframework.security.crypto.bcrypt.BCrypt.hashpw("plain", org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
        row.put("customid", 42);
        row.put("customer", "cust-alias");
        row.put("stripe_customer_id", "cus_123");
        when(jdbcTemplate.queryForList(anyString(), eq("user@example.com")))
                .thenReturn(List.of(row));

        Map<String, Object> claims = new HashMap<>();
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<Subscription> subsStatic = Mockito.mockStatic(Subscription.class, CALLS_REAL_METHODS)) {

            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, new HashMap<>(claims)));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt");

            SubscriptionCollection sc = mock(SubscriptionCollection.class);
            Subscription sub = mock(Subscription.class);
            when(sc.getData()).thenReturn(List.of(sub));
            subsStatic.when(() -> Subscription.list(anyMap())).thenReturn(sc);

            ResponseEntity<?> resp = controller.login(req, httpRequest, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertNotNull(body);
            assertEquals("new.jwt", body.get("token"));
            assertEquals(42, body.get("bouticid"));
            assertEquals("cust-alias", body.get("customer"));
            assertEquals("cus_123", body.get("stripecustomerid"));
            assertEquals("OK", body.get("subscriptionstatus"));
        }
    }

    @Test
    @DisplayName("login - trop de tentatives: 429")
    void login_tooManyAttempts() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("plain");
        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        when(jdbcTemplate.queryForObject(
                contains("SELECT COUNT(*) FROM connexion"),
                eq(Integer.class),
                any()))
                .thenReturn(10);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, new HashMap<>()));

            ResponseEntity<?> resp = controller.login(req, httpRequest, "Bearer tok");

            assertEquals(HttpStatus.TOO_MANY_REQUESTS, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("tentatives"));
        }
    }

    @Test
    @DisplayName("login - email inconnu: 401 et incrément des tentatives")
    void login_unknownEmail() {
        LoginRequest req = new LoginRequest();
        req.setEmail("unknown@example.com");
        req.setPassword("plain");
        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        when(jdbcTemplate.queryForObject(
                contains("SELECT COUNT(*) FROM connexion"),
                eq(Integer.class),
                any()))
                .thenReturn(0);

        when(jdbcTemplate.queryForList(anyString(), eq("unknown@example.com")))
                .thenReturn(List.of());

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, new HashMap<>()));

            ResponseEntity<?> resp = controller.login(req, httpRequest, "Bearer tok");

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Mauvais identifiant"));
            verify(jdbcTemplate).update(contains("INSERT INTO connexion (ip, ts) VALUES"), eq("1.2.3.4"));
        }
    }

    @Test
    @DisplayName("login - mauvais mot de passe: 401 et incrément des tentatives")
    void login_badPassword() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("wrong");
        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        when(jdbcTemplate.queryForObject(
                contains("SELECT COUNT(*) FROM connexion"),
                eq(Integer.class),
                any()))
                .thenReturn(0);

        Map<String, Object> row = new HashMap<>();
        row.put("pass", org.springframework.security.crypto.bcrypt.BCrypt.hashpw("plain", org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
        row.put("customid", 42);
        row.put("customer", "cust-alias");
        row.put("stripe_customer_id", "cus_123");
        when(jdbcTemplate.queryForList(anyString(), eq("user@example.com")))
                .thenReturn(List.of(row));

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, new HashMap<>()));

            ResponseEntity<?> resp = controller.login(req, httpRequest, "Bearer tok");

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Mauvais identifiant"));
            verify(jdbcTemplate).update(contains("INSERT INTO connexion (ip, ts) VALUES"), eq("1.2.3.4"));
        }
    }

    @Test
    @DisplayName("login - Stripe KO renvoie subscriptionstatus=KO mais 200")
    void login_stripeKo_still200() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("plain");
        when(httpRequest.getRemoteAddr()).thenReturn("1.2.3.4");

        when(jdbcTemplate.queryForObject(
                contains("SELECT COUNT(*) FROM connexion"),
                eq(Integer.class),
                any()))
                .thenReturn(0);

        Map<String, Object> row = new HashMap<>();
        row.put("pass", org.springframework.security.crypto.bcrypt.BCrypt.hashpw("plain", org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
        row.put("customid", 42);
        row.put("customer", "cust-alias");
        row.put("stripe_customer_id", "cus_123");
        when(jdbcTemplate.queryForList(anyString(), eq("user@example.com")))
                .thenReturn(List.of(row));

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<Subscription> subsStatic = Mockito.mockStatic(Subscription.class)) {

            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, new HashMap<>()));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt");

            subsStatic.when(() -> Subscription.list(anyMap()))
                    .thenThrow(new ApiException("err", null, "invalid_request", 400, null));

            ResponseEntity<?> resp = controller.login(req, httpRequest, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertEquals("KO", ((Map<?, ?>) resp.getBody()).get("subscriptionstatus"));
        }
    }

    @Test
    @DisplayName("googleSignIn - utilisateur inexistant -> status=OK et champs vides + verify_email dans payload")
    void googleSignIn_userNotFound() {
        GoogleSignInRequest req = new GoogleSignInRequest();
        req.setEmail("new@example.com");

        when(jdbcTemplate.queryForList(anyString(), eq("new@example.com")))
                .thenReturn(List.of());

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            Map<String, Object> payload = new HashMap<>();
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.googleSignIn(req, httpRequest, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("", body.get("bouticid"));
            assertEquals("", body.get("customer"));
            assertEquals("", body.get("stripecustomerid"));
            assertEquals("KO", body.get("subscriptionstatus"));
            assertEquals("KO", body.get("status"));
            assertEquals("", body.get("password"));
        }
    }

    @Test
    @DisplayName("googleSignIn - utilisateur existant -> OK avec données et token")
    void googleSignIn_userFound() {
        GoogleSignInRequest req = new GoogleSignInRequest();
        req.setEmail("user@example.com");

        Map<String, Object> row = new HashMap<>();
        row.put("pass", "hashed");
        row.put("customid", 42);
        row.put("customer", "cust-alias");
        row.put("stripe_customer_id", "cus_123");
        when(jdbcTemplate.queryForList(anyString(), eq("user@example.com")))
                .thenReturn(List.of(row));

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<Subscription> subsStatic = Mockito.mockStatic(Subscription.class)) {

            Map<String, Object> payload = new HashMap<>();
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt");

            SubscriptionCollection sc = mock(SubscriptionCollection.class);
            when(sc.getData()).thenReturn(List.of()); // vide => KO
            subsStatic.when(() -> Subscription.list(anyMap())).thenReturn(sc);

            ResponseEntity<?> resp = controller.googleSignIn(req, httpRequest, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("new.jwt", body.get("token"));
            assertEquals(42, body.get("bouticid"));
            assertEquals("cust-alias", body.get("customer"));
            assertEquals("cus_123", body.get("stripecustomerid"));
            assertEquals("KO", body.get("subscriptionstatus"));
            assertEquals("OK", body.get("status"));
            assertEquals("hashed", body.get("password"));
        }
    }

    private static void set(Object target, String field, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("Injection échouée: " + field + " - " + e.getMessage());
        }
    }
}