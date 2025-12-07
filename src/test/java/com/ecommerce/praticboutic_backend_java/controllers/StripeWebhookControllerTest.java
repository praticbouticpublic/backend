package com.ecommerce.praticboutic_backend_java.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.net.Webhook;
import com.stripe.param.SubscriptionListParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

// ... existing code ...

class StripeWebhookControllerTest {

    private StripeWebhookController controller;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        controller = new StripeWebhookController();
        jdbcTemplate = mock(JdbcTemplate.class, Answers.RETURNS_DEEP_STUBS);
        inject(controller, "jdbcTemplate", jdbcTemplate);
        inject(controller, "endpointSecret", "whsec_test");
    }

    @Test
    @DisplayName("handleStripeWebhook - 200 sur event valide sans action")
    void handleStripeWebhook_ok_noAction() {
        String payload = "{}";
        String sig = "t=123,v1=abc";

        Event event = mock(Event.class);
        when(event.getType()).thenReturn("charge.succeeded");
        when(event.getId()).thenReturn("evt_123");

        try (MockedStatic<Webhook> webhookStatic = Mockito.mockStatic(Webhook.class)) {
            webhookStatic.when(() -> Webhook.constructEvent(payload, sig, "whsec_test"))
                    .thenReturn(event);

            ResponseEntity<?> resp = controller.handleStripeWebhook(payload, sig);

            assertEquals(HttpStatus.OK, resp.getStatusCode());
        }
    }

    @Test
    @DisplayName("handleStripeWebhook - 200 et update appelé pour subscription.created")
    void handleStripeWebhook_subscriptionCreated() {
        String payload = "{}";
        String sig = "t=123,v1=abc";

        Event event = mock(Event.class);
        when(event.getType()).thenReturn("customer.subscription.created");
        when(event.getId()).thenReturn("evt_sub");

        EventDataObjectDeserializer deser = mock(EventDataObjectDeserializer.class);
        Subscription sub = mock(Subscription.class);
        when(deser.getObject()).thenReturn(java.util.Optional.of(sub));
        when(event.getDataObjectDeserializer()).thenReturn(deser);

        try (MockedStatic<Webhook> webhookStatic = Mockito.mockStatic(Webhook.class);
             MockedStatic<Subscription> subStatic = Mockito.mockStatic(Subscription.class)) {
            webhookStatic.when(() -> Webhook.constructEvent(payload, sig, "whsec_test"))
                    .thenReturn(event);

            // Lors de l'update, le contrôleur interroge Stripe et la base
            when(sub.getCustomer()).thenReturn("cus_123");
            SubscriptionCollection sc = mock(SubscriptionCollection.class);
            when(sc.getData()).thenReturn(List.of(sub));
            subStatic.when(() -> Subscription.list(any(SubscriptionListParams.class)))
                    .thenReturn(sc);

            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("cus_123")))
                    .thenReturn(42);
            when(jdbcTemplate.update(anyString(), anyInt(), anyInt())).thenReturn(1);

            ResponseEntity<?> resp = controller.handleStripeWebhook(payload, sig);

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            verify(jdbcTemplate).update(anyString(), eq(1), eq(42));
        }
    }

    @Test
    @DisplayName("handleStripeWebhook - 400 si signature invalide")
    void handleStripeWebhook_badSignature() {
        String payload = "{}";
        String sig = "bad";

        try (MockedStatic<Webhook> webhookStatic = Mockito.mockStatic(Webhook.class)) {
            webhookStatic.when(() -> Webhook.constructEvent(payload, sig, "whsec_test"))
                    .thenThrow(new SignatureVerificationException("bad", null));

            ResponseEntity<?> resp = controller.handleStripeWebhook(payload, sig);

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            assertEquals("Signature invalide", resp.getBody());
        }
    }

    @Test
    @DisplayName("handleStripeWebhook - 500 en cas d'exception generic")
    void handleStripeWebhook_genericError() {
        String payload = "{}";
        String sig = "sig";

        try (MockedStatic<Webhook> webhookStatic = Mockito.mockStatic(Webhook.class)) {
            webhookStatic.when(() -> Webhook.constructEvent(payload, sig, "whsec_test"))
                    .thenThrow(new RuntimeException("boom"));

            ResponseEntity<?> resp = controller.handleStripeWebhook(payload, sig);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("boom"));
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