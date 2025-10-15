package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailVerificationRequestTest {

    @Test
    void testSetAndGetSessionId() {
        // Arrange
        EmailVerificationRequest request = new EmailVerificationRequest();
        String expectedSessionId = "session_123";

        // Act
        request.setSessionId(expectedSessionId);

        // Assert
        assertEquals(expectedSessionId, request.getSessionId(),
                "Le getter doit retourner la valeur définie par le setter pour sessionId");
    }

    @Test
    void testSetAndGetEmail() {
        // Arrange
        EmailVerificationRequest request = new EmailVerificationRequest();
        String expectedEmail = "user@example.com";

        // Act
        request.setEmail(expectedEmail);

        // Assert
        assertEquals(expectedEmail, request.getEmail(),
                "Le getter doit retourner la valeur définie par le setter pour email");
    }

    @Test
    void testDefaultsToNull() {
        // Arrange
        EmailVerificationRequest request = new EmailVerificationRequest();

        // Assert
        assertAll(
                () -> assertNull(request.getSessionId(), "Le champ sessionId doit être null par défaut"),
                () -> assertNull(request.getEmail(), "Le champ email doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        // Arrange
        EmailVerificationRequest request = new EmailVerificationRequest();
        String expectedSessionId = "session_456";
        String expectedEmail = "contact@boutic.com";

        // Act
        request.setSessionId(expectedSessionId);
        request.setEmail(expectedEmail);

        // Assert
        assertAll(
                () -> assertEquals(expectedSessionId, request.getSessionId()),
                () -> assertEquals(expectedEmail, request.getEmail())
        );
    }
}
