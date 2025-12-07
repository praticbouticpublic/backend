package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChargeRequestTest {

    @Test
    void testSetAndGetBouticid() {
        // Arrange
        ChargeRequest request = new ChargeRequest();
        Integer expectedId = 101;

        // Act
        request.setBouticid(expectedId);

        // Assert
        assertEquals(expectedId, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testBouticidDefaultsToNull() {
        // Arrange
        ChargeRequest request = new ChargeRequest();

        // Assert
        assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut");
    }
}
