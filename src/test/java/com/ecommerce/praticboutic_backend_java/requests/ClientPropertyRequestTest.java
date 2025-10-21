package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientPropertyRequestTest {

    @Test
    void testSetAndGetBouticid() {
        // Arrange
        ClientPropertyRequest request = new ClientPropertyRequest();
        Long expectedId = 123L;

        // Act
        request.setBouticid(expectedId);

        // Assert
        assertEquals(expectedId, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetProp() {
        // Arrange
        ClientPropertyRequest request = new ClientPropertyRequest();
        String expectedProp = "subscriptionStatus";

        // Act
        request.setProp(expectedProp);

        // Assert
        assertEquals(expectedProp, request.getProp(),
                "Le getter doit retourner la valeur définie par le setter pour prop");
    }

    @Test
    void testDefaultsToNull() {
        // Arrange
        ClientPropertyRequest request = new ClientPropertyRequest();

        // Assert
        assertAll(
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getProp(), "Le champ prop doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        // Arrange
        ClientPropertyRequest request = new ClientPropertyRequest();
        Long expectedId = 456L;
        String expectedProp = "email";

        // Act
        request.setBouticid(expectedId);
        request.setProp(expectedProp);

        // Assert
        assertAll(
                () -> assertEquals(expectedId, request.getBouticid()),
                () -> assertEquals(expectedProp, request.getProp())
        );
    }
}
