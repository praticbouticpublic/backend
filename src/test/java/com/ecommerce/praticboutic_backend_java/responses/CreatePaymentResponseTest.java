package com.ecommerce.praticboutic_backend_java.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePaymentResponseTest {

    @Test
    void testDefaultConstructor() {
        // Act
        CreatePaymentResponse response = new CreatePaymentResponse();

        // Assert
        assertNull(response.getClientSecret(),
                "Le clientSecret doit être null avec le constructeur par défaut");
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        String expectedSecret = "cs_test_123456";

        // Act
        CreatePaymentResponse response = new CreatePaymentResponse(expectedSecret);

        // Assert
        assertEquals(expectedSecret, response.getClientSecret(),
                "Le constructeur paramétré doit correctement initialiser clientSecret");
    }

    @Test
    void testSetterAndGetter() {
        // Arrange
        CreatePaymentResponse response = new CreatePaymentResponse();
        String expectedSecret = "cs_test_abcdef";

        // Act
        response.setClientSecret(expectedSecret);

        // Assert
        assertEquals(expectedSecret, response.getClientSecret(),
                "Le getter doit retourner la valeur fixée par le setter");
    }

    @Test
    void testSetClientSecretWithNull() {
        // Arrange
        CreatePaymentResponse response = new CreatePaymentResponse();

        // Act
        response.setClientSecret(null);

        // Assert
        assertNull(response.getClientSecret(),
                "Le clientSecret doit pouvoir être défini à null sans erreur");
    }
}
