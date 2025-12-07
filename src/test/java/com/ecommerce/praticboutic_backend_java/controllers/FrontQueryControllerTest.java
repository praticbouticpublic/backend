package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.FrontQueryRequest;
import com.ecommerce.praticboutic_backend_java.services.*;
import com.ecommerce.praticboutic_backend_java.configurations.StripeConfig;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
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

class FrontQueryControllerTest {

    private FrontQueryController controller;

    // Services mockés
    private CategorieService categorieService;
    private ArticleService articleService;
    private RelGrpOptArtService relGrpOptArtService;
    private OptionService optionService;
    private CustomerService customerService;
    private ClientService clientService;
    private ImageService imageService;
    private AbonnementService abonnementService;
    private ParameterService paramService;
    private StripeConfig stripeConfig;
    private CustomerRepository customerRepository;
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        controller = new FrontQueryController();

        categorieService = mock(CategorieService.class, Answers.RETURNS_DEEP_STUBS);
        articleService = mock(ArticleService.class, Answers.RETURNS_DEEP_STUBS);
        relGrpOptArtService = mock(RelGrpOptArtService.class, Answers.RETURNS_DEEP_STUBS);
        optionService = mock(OptionService.class, Answers.RETURNS_DEEP_STUBS);
        customerService = mock(CustomerService.class, Answers.RETURNS_DEEP_STUBS);
        clientService = mock(ClientService.class, Answers.RETURNS_DEEP_STUBS);
        imageService = mock(ImageService.class, Answers.RETURNS_DEEP_STUBS);
        abonnementService = mock(AbonnementService.class, Answers.RETURNS_DEEP_STUBS);
        paramService = mock(ParameterService.class, Answers.RETURNS_DEEP_STUBS);
        stripeConfig = mock(StripeConfig.class, Answers.RETURNS_DEEP_STUBS);
        customerRepository = mock(CustomerRepository.class, Answers.RETURNS_DEEP_STUBS);
        clientRepository = mock(ClientRepository.class, Answers.RETURNS_DEEP_STUBS);

        inject(controller, "categorieService", categorieService);
        inject(controller, "articleService", articleService);
        inject(controller, "relGrpOptArtService", relGrpOptArtService);
        inject(controller, "optionService", optionService);
        inject(controller, "customerService", customerService);
        inject(controller, "clientService", clientService);
        inject(controller, "imageService", imageService);
        inject(controller, "abonnementService", abonnementService);
        inject(controller, "paramService", paramService);
        inject(controller, "stripeConfig", stripeConfig);
        inject(controller, "customerRepository", customerRepository);
        inject(controller, "clientRepository", clientRepository);
        inject(controller, "maxLifetime", 3600);
    }

    @Test
    @DisplayName("handleRequest - categories -> 200")
    void handleRequest_categories_ok() {
        FrontQueryRequest req = new FrontQueryRequest();
        req.setRequete("categories");
        req.setBouticid(42);
        // Aligne exactement le type de retour du service
        List<String> cats = Arrays.asList("c1", "c2");
        when(categorieService.getCategories(eq(42))).thenAnswer(inv -> cats);

        ResponseEntity<?> resp = controller.handleRequest(req, null, "Bearer tok");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(2, ((List<?>) resp.getBody()).size());
    }

    @Test
    @DisplayName("handleRequest - articles -> 200")
    void handleRequest_articles_ok() {
        FrontQueryRequest req = new FrontQueryRequest();
        req.setRequete("articles");
        req.setBouticid(42);
        req.setCatid(3);
        when(articleService.getArticles(42,3)).thenReturn(List.of(List.of(1,"A")));

        ResponseEntity<?> resp = controller.handleRequest(req, null, "Bearer tok");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, ((List<?>) resp.getBody()).size());
    }

    @Test
    @DisplayName("handleRequest - initSession -> 200 et token")
    void handleRequest_initSession_ok() {
        FrontQueryRequest req = new FrontQueryRequest();
        req.setRequete("initSession");
        req.setCustomer("cust");
        req.setMethod("3");
        req.setTable("0");

        Map<String,Object> payload = new HashMap<>();
        try (MockedStatic<com.ecommerce.praticboutic_backend_java.services.JwtService> jwtStatic = Mockito.mockStatic(com.ecommerce.praticboutic_backend_java.services.JwtService.class)) {
            jwtStatic.when(() -> com.ecommerce.praticboutic_backend_java.services.JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));
            jwtStatic.when(() -> com.ecommerce.praticboutic_backend_java.services.JwtService.generateToken(anyMap(), anyString()))
                    .thenReturn("new.jwt");

            ResponseEntity<?> resp = controller.handleRequest(req, null, "Bearer tok");
            assertEquals(HttpStatus.OK, resp.getStatusCode());
            List<?> list = (List<?>) resp.getBody();
            assertTrue(list.get(0) instanceof ResponseEntity<?>);
            ResponseEntity<?> inner = (ResponseEntity<?>) list.get(0);
            Map<?, ?> body = (Map<?, ?>) inner.getBody();
            assertEquals("new.jwt", body.get("token"));
        }
    }

    @Test
    @DisplayName("handleRequest - requête non supportée -> 400")
    void handleRequest_unsupported() {
        FrontQueryRequest req = new FrontQueryRequest();
        req.setRequete("unknown");
        ResponseEntity<?> resp = controller.handleRequest(req, null, "Bearer tok");
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
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