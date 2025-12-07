package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.requests.DepartCommandeRequest;
import com.ecommerce.praticboutic_backend_java.services.*;
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
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class DepartCommandeControllerTest {

    private DepartCommandeController controller;

    private EmailService emailService;
    private ParameterService paramService;
    private ClientRepository clientRepository;
    private NotificationService notificationService;
    private DepartCommandeService departCommandeService;
    private CustomerRepository customerRepository;
    private JdbcTemplate jdbcTemplate;
    private StripeService stripeService;
    private SmsService smsService;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class, Answers.RETURNS_DEEP_STUBS);
        paramService = mock(ParameterService.class, Answers.RETURNS_DEEP_STUBS);
        clientRepository = mock(ClientRepository.class, Answers.RETURNS_DEEP_STUBS);
        notificationService = mock(NotificationService.class, Answers.RETURNS_DEEP_STUBS);
        departCommandeService = mock(DepartCommandeService.class, Answers.RETURNS_DEEP_STUBS);
        customerRepository = mock(CustomerRepository.class, Answers.RETURNS_DEEP_STUBS);
        jdbcTemplate = mock(JdbcTemplate.class, Answers.RETURNS_DEEP_STUBS);
        stripeService = mock(StripeService.class, Answers.RETURNS_DEEP_STUBS);
        smsService = mock(SmsService.class, Answers.RETURNS_DEEP_STUBS);

        controller = new DepartCommandeController();

        inject(controller, "emailService", emailService);
        inject(controller, "paramService", paramService);
        inject(controller, "clientRepository", clientRepository);
        inject(controller, "notificationService", notificationService);
        inject(controller, "departCommandeService", departCommandeService);
        inject(controller, "customerRepository", customerRepository);
        inject(controller, "jdbcTemplate", jdbcTemplate);
        inject(controller, "stripeService", stripeService);
        inject(controller, "smsService", smsService);
        inject(controller, "applicationUrl", "https://app.local");
        inject(controller, "sendmail", "noreply@app.local");
    }

    @Test
    @DisplayName("creerDepartCommande - happy path: 200 et token")
    void creerDepartCommande_happyPath() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("remise", "5.0");
        input.put("fraislivr", "2.0");
        input.put("telephone", "0600000000");
        String authHeader = "Bearer tok123";

        Map<String, Object> claims = new HashMap<>();
        claims.put("customer", "cust-alias");
        claims.put("method", "X");
        claims.put("table", "Y");
        claims.put("cust-alias_mail", "non");

        Customer customer = new Customer();
        customer.setCustomId(42);
        customer.setCltid(100);
        customer.setCourriel("c@ex.com");

        Client client = new Client();
        client.setDevice_id("dev-1");

        when(customerRepository.findByCustomer("cust-alias")).thenReturn(customer);
        when(clientRepository.findClientById(100)).thenReturn(Optional.of(client));


        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok123"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, claims));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt.token");

            when(paramService.getParameterValue("CMPT_CMD", 42)).thenReturn("7");
            when(paramService.setValeurParam(eq("CMPT_CMD"), eq(42), anyString())).thenReturn(true);
            when(paramService.getParameterValue("Subject_mail", 42)).thenReturn("Sujet");
            doNothing().when(departCommandeService).sendEmail(eq("c@ex.com"), eq("Sujet"), anyString(), eq(input), any(Double[].class), eq("tok123"));
            when(departCommandeService.enregistreCommande(anyString(), eq(input), any(Double[].class), eq("tok123"))).thenReturn(555);
            when(paramService.getParameterValue("NEW_ORDER", 42)).thenReturn("0");
            when(paramService.setValeurParam(eq("NEW_ORDER"), eq(42), anyString())).thenReturn(true);
            when(notificationService.sendPushNotification(anyString(), anyString(), anyString())).thenReturn("OK");
            when(stripeService.recordSubscriptionUsage(eq(42), anyDouble(), anyDouble(), anyDouble())).thenReturn(true);
            when(paramService.getParameterValue("VALIDATION_SMS", 42)).thenReturn("oui");
            when(smsService.sendOrderSms(eq("oui"), eq(555), eq(42), eq("0600000000"))).thenReturn(true);



            ResponseEntity<?> resp = controller.creerDepartCommande(input, authHeader);
            Integer cmdId = departCommandeService.enregistreCommande("42", input, new Double[]{101.2}, JwtService.generateToken(anyMap(), anyString()));


            assertEquals(HttpStatus.OK, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).containsKey("token"));

            verify(customerRepository).findByCustomer("cust-alias");
            verify(clientRepository).findClientById(100);
            verify(paramService).getParameterValue("CMPT_CMD", 42);
            verify(paramService).setValeurParam(eq("CMPT_CMD"), eq(42), anyString());
            verify(paramService).getParameterValue("Subject_mail", 42);
            verify(departCommandeService).sendEmail(eq("c@ex.com"), eq("Sujet"), anyString(), eq(input), any(Double[].class), eq("tok123"));
            verify(departCommandeService).enregistreCommande(anyString(), eq(input), any(Double[].class), eq("tok123"));
            verify(paramService).getParameterValue("NEW_ORDER", 42);
            verify(paramService).setValeurParam(eq("NEW_ORDER"), eq(42), anyString());
            verify(notificationService).sendPushNotification(eq("dev-1"), anyString(), anyString());
            verify(stripeService).recordSubscriptionUsage(eq(42), anyDouble(), anyDouble(), anyDouble());
            verify(paramService).getParameterValue("VALIDATION_SMS", 42);
            verify(smsService).sendOrderSms(eq("oui"), eq(555), eq(42), eq("0600000000"));
        }
    }

    @Test
    @DisplayName("creerDepartCommande - 500 si 'customer' absent/vidé")
    void creerDepartCommande_missingCustomer() {
        Map<String, Object> input = new HashMap<>();
        String authHeader = "Bearer tokX";

        Map<String, Object> claims = new HashMap<>();
        // pas de 'customer'

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tokX"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, claims));

            ResponseEntity<?> resp = controller.creerDepartCommande(input, authHeader);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).containsKey("error"));
        }
    }

    @Test
    @DisplayName("creerDepartCommande - 500 si email déjà envoyé")
    void creerDepartCommande_emailDejaEnvoye() {
        Map<String, Object> input = new HashMap<>();
        String authHeader = "Bearer tokE";

        Map<String, Object> claims = new HashMap<>();
        claims.put("customer", "cust-alias");
        claims.put("method", "X");
        claims.put("table", "Y");
        claims.put("cust-alias_mail", "oui"); // déjà envoyé

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tokE"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, claims));

            ResponseEntity<?> resp = controller.creerDepartCommande(input, authHeader);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Email déjà envoyé"));
        }
    }

    @Test
    @DisplayName("creerDepartCommande - 500 si customer introuvable en base")
    void creerDepartCommande_customerNotFound() {
        Map<String, Object> input = new HashMap<>();
        String authHeader = "Bearer tokC";

        Map<String, Object> claims = new HashMap<>();
        claims.put("customer", "cust-alias");
        claims.put("method", "X");
        claims.put("table", "Y");
        claims.put("cust-alias_mail", "non");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tokC"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, claims));

            when(customerRepository.findByCustomer("cust-alias")).thenReturn(null);

            ResponseEntity<?> resp = controller.creerDepartCommande(input, authHeader);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Informations du customer introuvables"));
        }
    }

    @Test
    @DisplayName("creerDepartCommande - 500 si client introuvable pour cltid")
    void creerDepartCommande_clientNotFound() {
        Map<String, Object> input = new HashMap<>();
        String authHeader = "Bearer tokD";

        Map<String, Object> claims = new HashMap<>();
        claims.put("customer", "cust-alias");
        claims.put("method", "X");
        claims.put("table", "Y");
        claims.put("cust-alias_mail", "non");

        Customer customer = new Customer();
        customer.setCustomId(42);
        customer.setCltid(100);

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tokD"))
                    .thenReturn(new com.ecommerce.praticboutic_backend_java.models.JwtPayload(null, null, claims));

            when(customerRepository.findByCustomer("cust-alias")).thenReturn(customer);
            when(clientRepository.findClientById(100)).thenReturn(Optional.empty());

            ResponseEntity<?> resp = controller.creerDepartCommande(input, authHeader);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Client introuvable"));
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