package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.CreatePaymentRequest;
import com.ecommerce.praticboutic_backend_java.requests.Item;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class CreatePaymentControllerTest {

    private CreatePaymentController controller;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class, Answers.RETURNS_DEEP_STUBS);
        controller = new CreatePaymentController();
        set(controller, "jdbcTemplate", jdbcTemplate);
        set(controller, "sessionMaxLifetime", 3600L);
    }

    @Test
    @DisplayName("createPaymentIntent - happy path retourne 200 et client secret")
    void createPaymentIntent_happyPath() throws StripeException {
        // Prépare la requête
        CreatePaymentRequest req = new CreatePaymentRequest();
        req.setBoutic("cust-alias");
        req.setModel("LIVRER");
        req.setFraislivr(2.0);
        req.setCodepromo("PROMO10");

        Item item1 = new Item();
        item1.setId("A1");
        item1.setType("article");
        item1.setPrix(10.0);
        item1.setQt(2);

        Item item2 = new Item();
        item2.setId("O1");
        item2.setType("option");
        item2.setPrix(1.5);
        item2.setQt(1);

        req.setItems(List.of(item1, item2));

        // Payload JWT
        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", "cust-alias");
        payload.put("method", "X");
        payload.put("table", "Y");
        payload.put("cust-alias_mail", "non");

        // Stubs DB
        when(jdbcTemplate.queryForObject(eq("SELECT customid FROM customer WHERE customer = ?"),
                eq(Integer.class), eq("cust-alias")))
                .thenReturn(42);

        when(jdbcTemplate.queryForObject(
                eq("SELECT valeur FROM parametre WHERE nom = ? AND customid = ?"),
                eq(String.class),
                eq("STRIPE_ACCOUNT_ID"),
                eq(42)))
                .thenReturn("acct_123");

        // Prix article
        when(jdbcTemplate.queryForObject(
                eq("SELECT prix FROM article WHERE customid = ? AND artid = ?"),
                eq(Double.class),
                eq(42),
                eq("A1")
        )).thenReturn(10.0);

        // Prix option
        when(jdbcTemplate.queryForObject(
                eq("SELECT surcout FROM `option` WHERE customid = ? AND optid = ?"),
                eq(Double.class),
                eq(42),
                eq("O1")
        )).thenReturn(1.5);

        // Frais livraison barlivr
        when(jdbcTemplate.queryForObject(
                startsWith("SELECT surcout FROM barlivr"),
                eq(Double.class),
                eq(42),
                anyDouble(),
                anyDouble()
        )).thenReturn(2.0);

        // Taux promo
        when(jdbcTemplate.queryForObject(
                eq("SELECT taux FROM promotion WHERE customid = ? AND code = ? AND actif = 1"),
                eq(Double.class),
                eq(42),
                eq("PROMO10")
        )).thenReturn(10.0);

        // Mock statique JwtService.parseToken/generateToken
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<PaymentIntent> paymentIntentStatic = Mockito.mockStatic(PaymentIntent.class)) {

            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, payload));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt");

            PaymentIntent fake = mock(PaymentIntent.class);
            when(fake.getClientSecret()).thenReturn("pi_secret_123");
            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(fake);

            ResponseEntity<?> resp = controller.createPaymentIntent(req, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("new.jwt", body.get("token"));
            assertEquals("pi_secret_123", body.get("intent"));
        }
    }

    @Test
    @DisplayName("createPaymentIntent - boutique inconnue -> 500")
    void createPaymentIntent_unknownBoutic() {
        CreatePaymentRequest req = new CreatePaymentRequest();
        req.setBoutic("unknown");
        req.setItems(List.of(makeArticle("A1", 10.0, 1)));

        when(jdbcTemplate.queryForObject(eq("SELECT customid FROM customer WHERE customer = ?"),
                eq(Integer.class), eq("unknown")))
                .thenThrow(new EmptyResultDataAccessException(1));

        Map<String, Object> payload = defaultPayload("cust-alias");
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.createPaymentIntent(req, "Bearer tok");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Boutique non trouvée"));
        }
    }

    @Test
    @DisplayName("createPaymentIntent - panier vide -> 500")
    void createPaymentIntent_emptyCart() {
        CreatePaymentRequest req = new CreatePaymentRequest();
        req.setBoutic("cust-alias");
        req.setItems(List.of()); // vide

        when(jdbcTemplate.queryForObject(eq("SELECT customid FROM customer WHERE customer = ?"),
                eq(Integer.class), eq("cust-alias")))
                .thenReturn(42);

        Map<String, Object> payload = defaultPayload("cust-alias");
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.createPaymentIntent(req, "Bearer tok");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Panier Vide"));
        }
    }

    @Test
    @DisplayName("createPaymentIntent - prix invalide -> 500")
    void createPaymentIntent_invalidPrice() {
        CreatePaymentRequest req = new CreatePaymentRequest();
        req.setBoutic("cust-alias");

        Item item = makeArticle("A1", 9.99, 1); // client envoie 9.99
        req.setItems(List.of(item));

        when(jdbcTemplate.queryForObject(eq("SELECT customid FROM customer WHERE customer = ?"),
                eq(Integer.class), eq("cust-alias")))
                .thenReturn(42);

        // Serveur dit 10.0 -> mismatch
        when(jdbcTemplate.queryForObject(
                eq("SELECT prix FROM article WHERE customid = ? AND artid = ?"),
                eq(Double.class),
                eq(42),
                eq("A1")
        )).thenReturn(10.0);

        Map<String, Object> payload = defaultPayload("cust-alias");
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.createPaymentIntent(req, "Bearer tok");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Prix invalide"));
        }
    }

    @Test
    @DisplayName("createPaymentIntent - Stripe renvoie erreur -> 502")
    void createPaymentIntent_stripeError() throws StripeException {
        CreatePaymentRequest req = new CreatePaymentRequest();
        req.setBoutic("cust-alias");
        req.setItems(List.of(makeArticle("A1", 10.0, 1)));

        when(jdbcTemplate.queryForObject(eq("SELECT customid FROM customer WHERE customer = ?"),
                eq(Integer.class), eq("cust-alias")))
                .thenReturn(42);

        when(jdbcTemplate.queryForObject(
                eq("SELECT valeur FROM parametre WHERE nom = ? AND customid = ?"),
                eq(String.class),
                eq("STRIPE_ACCOUNT_ID"),
                eq(42)))
                .thenReturn("acct_123");

        when(jdbcTemplate.queryForObject(
                eq("SELECT prix FROM article WHERE customid = ? AND artid = ?"),
                eq(Double.class),
                eq(42),
                eq("A1")
        )).thenReturn(10.0);

        Map<String, Object> payload = defaultPayload("cust-alias");
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<PaymentIntent> paymentIntentStatic = Mockito.mockStatic(PaymentIntent.class)) {

            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, payload));

            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenThrow(new ApiException("err", null, "invalid_request", 400, null));

            ResponseEntity<?> resp = controller.createPaymentIntent(req, "Bearer tok");
            assertEquals(HttpStatus.BAD_GATEWAY, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Erreur Stripe"));
        }
    }

    @Test
    @DisplayName("createPaymentIntent - garde-fous session: customer/mail manquants -> 500")
    void createPaymentIntent_missingSessionData() {
        CreatePaymentRequest req = new CreatePaymentRequest();
        req.setBoutic("cust-alias");
        req.setItems(List.of(makeArticle("A1", 10.0, 1)));

        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", ""); // manquant

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.createPaymentIntent(req, "Bearer tok");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Pas de boutic"));
        }
    }

    private Item makeArticle(String id, double prix, int qt) {
        Item it = new Item();
        it.setId(id);
        it.setType("article");
        it.setPrix(prix);
        it.setQt(qt);
        return it;
    }

    private Map<String, Object> defaultPayload(String customer) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("customer", customer);
        payload.put("method", "X");
        payload.put("table", "Y");
        payload.put(customer + "_mail", "non");
        return payload;
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