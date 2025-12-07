package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.ChargeRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.ParameterService;
// ... existing code ...
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

// ... existing code ...

class ChargeControllerTest {

    private ChargeController controller;

    private JwtService jwtService;
    private ParameterService parameterService;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class, Answers.RETURNS_DEEP_STUBS);
        parameterService = mock(ParameterService.class, Answers.RETURNS_DEEP_STUBS);

        controller = new ChargeController(); // constructeur par défaut

        // injecter les champs @Autowired/@Value via réflexion
        setField(controller, "jwtService", jwtService);
        setField(controller, "parameterService", parameterService);
    }

    @Test
    @DisplayName("checkStripeAccount - retourne OK si compte Stripe chargeable")
    void checkStripeAccount_returnsOK_whenChargesEnabled() throws Exception {
        ChargeRequest req = new ChargeRequest();
        req.setBouticid(10);
        String auth = "Bearer token123";

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            JwtPayload payload = mock(JwtPayload.class);
            jwtStatic.when(() -> JwtService.parseToken("token123"))
                    .thenReturn(payload);

            when(jwtService.isAuthenticated(anyMap())).thenReturn(true);

            when(jwtService.isAuthenticated(anyMap())).thenReturn(true);
            when(parameterService.getParameterValue("STRIPE_ACCOUNT_ID", 10)).thenReturn("acct_1ABC");

            try (var accountMocked = Mockito.mockStatic(com.stripe.model.Account.class)) {
                com.stripe.model.Account fake = mock(com.stripe.model.Account.class);
                when(fake.getChargesEnabled()).thenReturn(true);
                accountMocked.when(() -> com.stripe.model.Account.retrieve("acct_1ABC")).thenReturn(fake);

                ResponseEntity<?> resp = controller.checkStripeAccount(req, auth);

                assertEquals(HttpStatus.OK, resp.getStatusCode());
                assertEquals(Map.of("result", "OK"), resp.getBody());
            }
        }

        verify(jwtService).isAuthenticated(anyMap());
        verify(parameterService).getParameterValue("STRIPE_ACCOUNT_ID", 10);
        verifyNoMoreInteractions(jwtService, parameterService);
    }

    @Test
    @DisplayName("checkStripeAccount - retourne KO si pas d'ID Stripe")
    void checkStripeAccount_returnsKO_whenMissingStripeAccountId() throws Exception {
        ChargeRequest req = new ChargeRequest();
        req.setBouticid(11);
        String auth = "Bearer tokenABC";

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            JwtPayload payload = mock(JwtPayload.class);
            jwtStatic.when(() -> JwtService.parseToken("tokenABC"))
                    .thenReturn(payload);
            when(jwtService.isAuthenticated(anyMap())).thenReturn(true);
            when(parameterService.getParameterValue("STRIPE_ACCOUNT_ID", 11)).thenReturn("");

            ResponseEntity<?> resp = controller.checkStripeAccount(req, auth);

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertEquals(Map.of("result", "KO"), resp.getBody());
        }

        verify(jwtService).isAuthenticated(anyMap());
        verify(parameterService).getParameterValue("STRIPE_ACCOUNT_ID", 11);
        verifyNoMoreInteractions(jwtService, parameterService);
    }

    @Test
    @DisplayName("checkStripeAccount - 500 si non authentifié")
    void checkStripeAccount_returnsError_whenUnauthenticated() throws Exception {
        ChargeRequest req = new ChargeRequest();
        req.setBouticid(12);
        String auth = "Bearer tokenZ";

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            JwtPayload payload = mock(JwtPayload.class);
            jwtStatic.when(() -> JwtService.parseToken("tokenZ"))
                    .thenReturn(payload);
            when(jwtService.isAuthenticated(anyMap())).thenReturn(false);

            ResponseEntity<?> resp = controller.checkStripeAccount(req, auth);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Non authentifié"));
        }

        verify(jwtService).isAuthenticated(anyMap());
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(parameterService);
    }

    @Test
    @DisplayName("checkStripeAccount - 500 si ID boutique manquant")
    void checkStripeAccount_returnsError_whenBouticIdMissing() throws Exception {
        ChargeRequest req = new ChargeRequest(); // bouticid null
        String auth = "Bearer tokenY";

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            JwtPayload payload = mock(JwtPayload.class);
            jwtStatic.when(() -> JwtService.parseToken("tokenY"))
                    .thenReturn(payload);
            when(jwtService.isAuthenticated(anyMap())).thenReturn(true);

            ResponseEntity<?> resp = controller.checkStripeAccount(req, auth);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("ID de boutique manquant"));
        }

        verify(jwtService).isAuthenticated(anyMap());
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(parameterService);
    }

    @Test
    @DisplayName("checkStripeAccount - retourne KO si chargesEnabled = false")
    void checkStripeAccount_returnsKO_whenChargesDisabled() throws Exception {
        ChargeRequest req = new ChargeRequest();
        req.setBouticid(15);
        String auth = "Bearer tokenKO";

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            JwtPayload payload = mock(JwtPayload.class);
            jwtStatic.when(() -> JwtService.parseToken("tokenKO"))
                    .thenReturn(payload);
            when(jwtService.isAuthenticated(anyMap())).thenReturn(true);
            when(parameterService.getParameterValue("STRIPE_ACCOUNT_ID", 15)).thenReturn("acct_disabled");

            try (var accountMocked = Mockito.mockStatic(com.stripe.model.Account.class)) {
                com.stripe.model.Account fake = mock(com.stripe.model.Account.class);
                when(fake.getChargesEnabled()).thenReturn(false);
                accountMocked.when(() -> com.stripe.model.Account.retrieve("acct_disabled")).thenReturn(fake);

                ResponseEntity<?> resp = controller.checkStripeAccount(req, auth);

                assertEquals(HttpStatus.OK, resp.getStatusCode());
                assertEquals(Map.of("result", "KO"), resp.getBody());
            }
        }

        verify(jwtService).isAuthenticated(anyMap());
        verify(parameterService).getParameterValue("STRIPE_ACCOUNT_ID", 15);
        verifyNoMoreInteractions(jwtService, parameterService);
    }
}