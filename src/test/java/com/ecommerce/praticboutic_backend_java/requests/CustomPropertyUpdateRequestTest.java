package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomPropertyUpdateRequestTest {

    @Test
    void testSetAndGetBouticid() {
        // Arrange
        CustomPropertyUpdateRequest request = new CustomPropertyUpdateRequest();
        Long expectedId = 789L;

        // Act
        request.setBouticid(expectedId);

        // Assert
        assertEquals(expectedId, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetProp() {
        // Arrange
        CustomPropertyUpdateRequest request = new CustomPropertyUpdateRequest();
        String expectedProp = "subscriptionStatus";

        // Act
        request.setProp(expectedProp);

        // Assert
        assertEquals(expectedProp, request.getProp(),
                "Le getter doit retourner la valeur définie par le setter pour prop");
    }

    @Test
    void testSetAndGetValeur() {
        // Arrange
        CustomPropertyUpdateRequest request = new CustomPropertyUpdateRequest();
        String expectedValeur = "active";

        // Act
        request.setValeur(expectedValeur);

        // Assert
        assertEquals(expectedValeur, request.getValeur(),
                "Le getter doit retourner la valeur définie par le setter pour valeur");
    }

    @Test
    void testDefaultsToNull() {
        // Arrange
        CustomPropertyUpdateRequest request = new CustomPropertyUpdateRequest();

        // Assert
        assertAll(
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getProp(), "Le champ prop doit être null par défaut"),
                () -> assertNull(request.getValeur(), "Le champ valeur doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        // Arrange
        CustomPropertyUpdateRequest request = new CustomPropertyUpdateRequest();
        Long expectedId = 321L;
        String expectedProp = "email";
        String expectedValeur = "newemail@example.com";

        // Act
        request.setBouticid(expectedId);
        request.setProp(expectedProp);
        request.setValeur(expectedValeur);

        // Assert
        assertAll(
                () -> assertEquals(expectedId, request.getBouticid()),
                () -> assertEquals(expectedProp, request.getProp()),
                () -> assertEquals(expectedValeur, request.getValeur())
        );
    }
}
