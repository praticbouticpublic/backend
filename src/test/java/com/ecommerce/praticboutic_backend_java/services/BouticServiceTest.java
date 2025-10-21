package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Customer;
// ... existing code ...
import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class BouticServiceTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private BouticService bouticService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    // ... existing code ...

    // ... existing code ...

    @Test
    @DisplayName("updateSessionAfterBoutiqueCreation - gère customer null")
    void updateSessionAfterBoutiqueCreation_handlesNullCustomer() {
        String token = "jwt-token";

        bouticService.updateSessionAfterBoutiqueCreation(null, token);

        // Si rien n'est posé en cas de null:
        verifyNoInteractions(sessionService);
        verifyNoMoreInteractions(httpSession);
    }

    // ... existing code ...

    @Test
    @DisplayName("updateSessionAfterBoutiqueCreation - gère token null")
    void updateSessionAfterBoutiqueCreation_handlesNullToken() {
        Customer customer = new Customer();
        customer.setCustomId(1);
        customer.setCustomer("alias");

        doNothing().when(httpSession).setAttribute(eq("customer_id"), any());
        doNothing().when(httpSession).setAttribute(eq("customer_alias"), any());

        bouticService.updateSessionAfterBoutiqueCreation(customer, null);

        verify(httpSession).setAttribute("customer_id", 1);
        verify(httpSession).setAttribute("customer_alias", "alias");
        // pas d’attribut auth_token si null
        verifyNoMoreInteractions(httpSession);
        verifyNoInteractions(sessionService);
    }


    @Test
    void updateSessionAfterBoutiqueCreation_setsSessionAttributes() {
        Customer customer = new Customer();
        customer.setCustomId(123);
        customer.setCustomer("alias-xyz");
        String token = "jwt-token";

        // Mock payload mutable
        JwtPayload mockPayload = mock(JwtPayload.class);
        Map<String, Object> claims = new HashMap<>();
        when(mockPayload.getClaims()).thenReturn(claims);

        // Mock static JwtService.parseToken
        try (var mocked = mockStatic(JwtService.class)) {
            mocked.when(() -> JwtService.parseToken(token)).thenReturn(mockPayload);

            bouticService.updateSessionAfterBoutiqueCreation(customer, token);

            verify(httpSession).setAttribute("customer_id", 123);
            verify(httpSession).setAttribute("customer_alias", "alias-xyz");
            verify(httpSession).setAttribute("auth_token", "jwt-token");
        }
    }


}