package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.ChargeRequest;
import com.ecommerce.praticboutic_backend_java.services.ParameterService;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import com.stripe.Stripe;
import com.stripe.model.Account;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChargeControllerTest {

    @InjectMocks
    private ChargeController chargeController;

    @Mock
    private SessionService sessionService;

    @Mock
    private ParameterService parameterService;

    @Mock
    private HttpSession httpSession;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckStripeAccount_authenticatedAndChargesEnabled_returnsOK() throws Exception {
        // Arrange
        ChargeRequest request = new ChargeRequest();
        request.setBouticid(1);

        when(sessionService.isAuthenticated()).thenReturn(true);
        when(parameterService.getParameterValue("STRIPE_ACCOUNT_ID", 1)).thenReturn("acct_test_123");

        Account mockAccount = mock(Account.class);
        when(mockAccount.getChargesEnabled()).thenReturn(true);

        // Stripe redéfinition statique
        try (MockedStatic<Account> mockedAccount = mockStatic(Account.class)) {
            mockedAccount.when(() -> Account.retrieve("acct_test_123")).thenReturn(mockAccount);

            // Act
            ResponseEntity<?> response = chargeController.checkStripeAccount(request, String.valueOf(httpSession));

            // Assert
            assertEquals(200, response.getStatusCodeValue());
            assertEquals("OK", response.getBody());
        }
    }

    @Test
    public void testCheckStripeAccount_notAuthenticated_returnsUnauthorized() {
        // Arrange
        ChargeRequest request = new ChargeRequest();
        when(sessionService.isAuthenticated()).thenReturn(false);

        // Act
        ResponseEntity<?> response = chargeController.checkStripeAccount(request, String.valueOf(httpSession));

        // Assert
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    public void testCheckStripeAccount_missingBouticId_returnsBadRequest() {
        // Arrange
        ChargeRequest request = new ChargeRequest(); // pas de setBouticId

        when(sessionService.isAuthenticated()).thenReturn(true);

        // Act
        ResponseEntity<?> response = chargeController.checkStripeAccount(request, String.valueOf(httpSession));

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testCheckStripeAccount_accountIdNotFound_returnsKO() {
        // Arrange
        ChargeRequest request = new ChargeRequest();
        request.setBouticid(2);

        when(sessionService.isAuthenticated()).thenReturn(true);
        when(parameterService.getParameterValue("STRIPE_ACCOUNT_ID", 2)).thenReturn(null);

        // Act
        ResponseEntity<?> response = chargeController.checkStripeAccount(request, String.valueOf(httpSession));

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("KO", response.getBody());
    }
}
