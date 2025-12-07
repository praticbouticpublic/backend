package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.RegistrationRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
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
import static org.mockito.Mockito.*;

// ... existing code ...

class RegistrationControllerTest {

    private RegistrationController controller;

    @BeforeEach
    void setUp() {
        controller = new RegistrationController();
        inject(controller, "sessionMaxLifetime", 3600);
    }

    @Test
    @DisplayName("registerMobile - email vérifié -> OK + token")
    void registerMobile_ok() throws Exception {
        RegistrationRequest req = new RegistrationRequest();
        req.pass = "p@ss";
        req.qualite = "M";
        req.nom = "Nom";
        req.prenom = "Prenom";
        req.adr1 = "1 rue";
        req.adr2 = "2 rue";
        req.cp = "75001";
        req.ville = "Paris";
        req.tel = "0600000000";

        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<Customer> customerStatic = Mockito.mockStatic(Customer.class)) {

            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt");

            Customer customer = mock(Customer.class);
            when(customer.getId()).thenReturn("cus_123");
            customerStatic.when(() -> Customer.create(any(CustomerCreateParams.class))).thenReturn(customer);

            ResponseEntity<?> resp = controller.registerMobile(req, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("OK", body.get("result"));
            assertEquals("new.jwt", body.get("token"));
        }
    }

    @Test
    @DisplayName("registerMobile - email non vérifié -> 500")
    void registerMobile_emailNotVerified() {
        RegistrationRequest req = new RegistrationRequest();
        Map<String, Object> payload = new HashMap<>(); // pas de verify_email

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.registerMobile(req, "Bearer tok");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Courriel non vérifié"));
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