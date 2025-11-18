package com.ecommerce.praticboutic_backend_java.responses;

import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testConstructorSetsError() {
        // Arrange
        String expectedError = "Paiement refusé";

        // Act
        ErrorResponse response = new ErrorResponse(expectedError);

        // Assert
        assertEquals(expectedError, response.getError(),
                "Le constructeur doit initialiser correctement le champ error");
    }

    @Test
    void testSetterAndGetter() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Erreur initiale");
        String expectedError = "Erreur mise à jour";

        // Act
        response.setError(expectedError);

        // Assert
        assertEquals(expectedError, response.getError(),
                "Le setter doit mettre à jour la valeur de error et le getter doit la retourner");
    }

    @Test
    void testSetErrorToNull() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Erreur temporaire");

        // Act
        response.setError(null);

        // Assert
        assertNull(response.getError(),
                "Le champ error doit pouvoir être défini à null sans provoquer d’erreur");
    }
}