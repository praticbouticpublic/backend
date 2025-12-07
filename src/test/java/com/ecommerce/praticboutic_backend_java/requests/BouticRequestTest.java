package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BouticRequestTest {

    @Test
    void testSetAndGetAliasboutic() {
        // Arrange
        BouticRequest request = new BouticRequest();
        String expectedAlias = "myBoutic";

        // Act
        request.setAliasboutic(expectedAlias);

        // Assert
        assertEquals(expectedAlias, request.getAliasboutic(),
                "Le getter doit retourner la valeur définie par le setter pour aliasboutic");
    }

    @Test
    void testSetAndGetNom() {
        // Arrange
        BouticRequest request = new BouticRequest();
        String expectedNom = "Boutic Test";

        // Act
        request.setNom(expectedNom);

        // Assert
        assertEquals(expectedNom, request.getNom(),
                "Le getter doit retourner la valeur définie par le setter pour nom");
    }

    @Test
    void testSetAndGetLogo() {
        // Arrange
        BouticRequest request = new BouticRequest();
        String expectedLogo = "logo.png";

        // Act
        request.setLogo(expectedLogo);

        // Assert
        assertEquals(expectedLogo, request.getLogo(),
                "Le getter doit retourner la valeur définie par le setter pour logo");
    }

    @Test
    void testSetAndGetEmail() {
        // Arrange
        BouticRequest request = new BouticRequest();
        String expectedEmail = "contact@boutic.com";

        // Act
        request.setEmail(expectedEmail);

        // Assert
        assertEquals(expectedEmail, request.getEmail(),
                "Le getter doit retourner la valeur définie par le setter pour email");
    }

    @Test
    void testAllFieldsTogether() {
        // Arrange
        BouticRequest request = new BouticRequest();
        String expectedAlias = "myBoutic";
        String expectedNom = "Boutic Test";
        String expectedLogo = "logo.png";
        String expectedEmail = "contact@boutic.com";

        // Act
        request.setAliasboutic(expectedAlias);
        request.setNom(expectedNom);
        request.setLogo(expectedLogo);
        request.setEmail(expectedEmail);

        // Assert
        assertAll(
                () -> assertEquals(expectedAlias, request.getAliasboutic()),
                () -> assertEquals(expectedNom, request.getNom()),
                () -> assertEquals(expectedLogo, request.getLogo()),
                () -> assertEquals(expectedEmail, request.getEmail())
        );
    }
}

