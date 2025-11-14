package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.configurations.StripeConfig;
import com.ecommerce.praticboutic_backend_java.entities.Abonnement;
import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.repositories.AbonnementRepository;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.requests.LiensRequest;
import com.ecommerce.praticboutic_backend_java.requests.SubscriptionRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.google.gson.Gson;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.param.SubscriptionListParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

// ... existing code ...

class SubscriptionControllerTest {

    private SubscriptionController controller;

    private StripeConfig stripeConfig;
    private AbonnementRepository abonnementRepository;
    private ClientRepository clientRepository;
    private Environment environment;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stripeConfig = mock(StripeConfig.class, Answers.RETURNS_DEEP_STUBS);
        abonnementRepository = mock(AbonnementRepository.class, Answers.RETURNS_DEEP_STUBS);
        clientRepository = mock(ClientRepository.class, Answers.RETURNS_DEEP_STUBS);
        environment = mock(Environment.class, Answers.RETURNS_DEEP_STUBS);
        dataSource = mock(DataSource.class, Answers.RETURNS_DEEP_STUBS);
        jdbcTemplate = mock(JdbcTemplate.class, Answers.RETURNS_DEEP_STUBS);

        controller = new SubscriptionController(stripeConfig);
        inject(controller, "abonnementRepository", abonnementRepository);
        inject(controller, "clientRepository", clientRepository);
        inject(controller, "environment", environment);
        inject(controller, "dataSource", dataSource);
        inject(controller, "jdbcTemplate", jdbcTemplate);
        inject(controller, "stripePublicKey", "pk_test_x");
    }

    @Test
    @DisplayName("getLiensCreationBoutic - non authentifié -> Exception")
    void getLiensCreationBoutic_unauthenticated() {
        LiensRequest req = new LiensRequest();
        req.setLogin("user@example.com");

        Map<String, Object> payload = new HashMap<>();
        // pas de bo_auth
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));
            assertThrows(Exception.class, () -> controller.getLiensCreationBoutic(req, "Bearer tok"));
        }
    }

    @Test
    @DisplayName("getLiensCreationBoutic - OK renvoie data liste (mock Stripe static)")
    void getLiensCreationBoutic_ok() throws Exception {
        LiensRequest req = new LiensRequest();
        req.setLogin("user@example.com");

        Map<String, Object> payload = new HashMap<>();
        payload.put("bo_auth", "oui");

        Client cli = new Client();
        cli.setStripeCustomerId("cus_123");
        when(clientRepository.findByEmailAndActif("user@example.com", 1)).thenReturn(Optional.of(cli));

        Abonnement ab = new Abonnement();
        ab.setAboId(1);
        ab.setCreationBoutic(false);
        ab.setBouticId(42);
        ab.setStripeSubscriptionId("sub_123");
        when(abonnementRepository.findByCltId(cli.getCltId())).thenReturn(List.of(ab));

        // Mock statique Subscription.retrieve car StripeClient n'est pas disponible ici
        Subscription subscription = mock(Subscription.class);
        when(subscription.toJson()).thenReturn("{\"id\":\"sub_123\"}");

        try (MockedStatic<Subscription> subStatic = Mockito.mockStatic(Subscription.class);
             MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {

            subStatic.when(() -> Subscription.retrieve("sub_123")).thenReturn(subscription);
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.getLiensCreationBoutic(req, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("OK", body.get("status"));
            assertTrue(body.containsKey("data"));
        }
    }

    @Test
    @DisplayName("getConfiguration - courriel non vérifié -> Exception")
    void getConfiguration_unverified() {
        Map<String, Object> payload = new HashMap<>();
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));
            assertThrows(Exception.class, () -> controller.getConfiguration("Bearer tok"));
        }
    }

    @Test
    @DisplayName("getConfiguration - OK retourne publishableKey + prices")
    void getConfiguration_ok() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        Price price = mock(Price.class);
        PriceCollection prices = mock(PriceCollection.class);
        when(prices.getData()).thenReturn(List.of(price));

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<Price> priceStatic = Mockito.mockStatic(Price.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));
            priceStatic.when(() -> Price.list(anyMap())).thenReturn(prices);

            Map<String, Object> resp = controller.getConfiguration("Bearer tok");

            assertEquals("pk_test_x", resp.get("publishableKey"));
            assertEquals(List.of(price), resp.get("prices"));
        }
    }

    @Test
    @DisplayName("check-subscription - OK -> result selon Stripe et actif DB")
    void checkSubscription_ok() {
        Map<String, Object> payload = new HashMap<>();
        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<Subscription> subStatic = Mockito.mockStatic(Subscription.class)) {

            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));

            SubscriptionCollection sc = mock(SubscriptionCollection.class);
            when(sc.getData()).thenReturn(List.of(mock(Subscription.class)));
            subStatic.when(() -> Subscription.list(any(SubscriptionListParams.class))).thenReturn(sc);

            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), any()))
                    .thenReturn(1);

            ResponseEntity<?> resp = controller.checkSubscription(Map.of("stripecustomerid", "cus_123"), "Bearer tok");
            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertEquals("OK", ((Map<?, ?>) resp.getBody()).get("result"));
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