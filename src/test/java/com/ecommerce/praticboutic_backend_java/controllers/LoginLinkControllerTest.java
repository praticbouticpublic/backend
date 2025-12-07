package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.ParameterService;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.LoginLinkCreateOnAccountParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

// ... existing code ...

class LoginLinkControllerTest {

    private LoginLinkController controller;

    private CustomerRepository customerRepository;
    private ParameterService paramService;
    private ClientRepository clientRepository;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        controller = new LoginLinkController();

        customerRepository = mock(CustomerRepository.class, Answers.RETURNS_DEEP_STUBS);
        paramService = mock(ParameterService.class, Answers.RETURNS_DEEP_STUBS);
        clientRepository = mock(ClientRepository.class, Answers.RETURNS_DEEP_STUBS);
        jwtService = mock(JwtService.class, Answers.RETURNS_DEEP_STUBS);

        inject(controller, "customerRepository", customerRepository);
        inject(controller, "paramService", paramService);
        inject(controller, "clientRepository", clientRepository);
        inject(controller, "jwtService", jwtService);
        inject(controller, "appVersion", "1.0.0");
        inject(controller, "baseUrl", "https://app.local");
    }

    @Test
    @DisplayName("createLoginLink - non authentifié -> 401")
    void createLoginLink_unauthenticated() {
        when(jwtService.isAuthenticated(anyMap())).thenReturn(false);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, new HashMap<>()));

            ResponseEntity<?> resp = controller.createLoginLink(new com.ecommerce.praticboutic_backend_java.requests.LoginLinkRequest(), "Bearer tok");
            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        }
    }

    @Test
    @DisplayName("createLoginLink - compte Stripe existant, details soumis -> login link")
    void createLoginLink_existingAccount_detailsSubmitted() throws Exception {
        when(jwtService.isAuthenticated(anyMap())).thenReturn(true);

        Map<String, Object> payload = new HashMap<>();
        payload.put("bo_email", "user@example.com");

        Client client = new Client();
        client.setStripeCustomerId("cus_123");

        when(clientRepository.findByEmailAndActif("user@example.com", 1)).thenReturn(Optional.of(client));
        when(paramService.getValeur("STRIPE_ACCOUNT_ID", 42)).thenReturn("acct_123");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<Subscription> subsStatic = Mockito.mockStatic(Subscription.class);
             MockedStatic<Account> accountStatic = Mockito.mockStatic(Account.class);
             MockedStatic<LoginLink> loginLinkStatic = Mockito.mockStatic(LoginLink.class)) {

            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString())).thenReturn("new.jwt");

            SubscriptionCollection sc = mock(SubscriptionCollection.class);
            when(sc.getData()).thenReturn(List.of(mock(Subscription.class)));
            subsStatic.when(() -> Subscription.list(anyMap())).thenReturn(sc);

            Account account = mock(Account.class);
            when(account.getDetailsSubmitted()).thenReturn(true);
            accountStatic.when(() -> Account.retrieve("acct_123")).thenReturn(account);

            LoginLink link = mock(LoginLink.class);
            when(link.getUrl()).thenReturn("https://stripe.com/login");
            loginLinkStatic.when(() -> LoginLink.createOnAccount(eq("acct_123"), any(LoginLinkCreateOnAccountParams.class)))
                    .thenReturn(link);

            com.ecommerce.praticboutic_backend_java.requests.LoginLinkRequest req =
                    new com.ecommerce.praticboutic_backend_java.requests.LoginLinkRequest();
            req.setBouticid(42);
            req.setPlatform("web");

            ResponseEntity<?> resp = controller.createLoginLink(req, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("new.jwt", body.get("token"));
            assertEquals("https://stripe.com/login", body.get("url"));
        }
    }

    @Test
    @DisplayName("createLoginLink - pas d'abonnement actif -> 400")
    void createLoginLink_noActiveSubscription() {
        when(jwtService.isAuthenticated(anyMap())).thenReturn(true);

        Map<String, Object> payload = new HashMap<>();
        payload.put("bo_email", "user@example.com");

        Client client = new Client();
        client.setStripeCustomerId("cus_123");
        when(clientRepository.findByEmailAndActif("user@example.com", 1)).thenReturn(Optional.of(client));

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class);
             MockedStatic<Subscription> subsStatic = Mockito.mockStatic(Subscription.class)) {

            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));
            SubscriptionCollection sc = mock(SubscriptionCollection.class);
            when(sc.getData()).thenReturn(List.of());
            subsStatic.when(() -> Subscription.list(anyMap())).thenReturn(sc);

            com.ecommerce.praticboutic_backend_java.requests.LoginLinkRequest req =
                    new com.ecommerce.praticboutic_backend_java.requests.LoginLinkRequest();
            req.setBouticid(42);
            req.setPlatform("web");

            ResponseEntity<?> resp = controller.createLoginLink(req, "Bearer tok");
            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
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
