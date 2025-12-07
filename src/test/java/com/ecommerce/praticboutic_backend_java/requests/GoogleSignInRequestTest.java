package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GoogleSignInRequestTest {
    @Test
    void testSetAndGetEmail() {
        GoogleSignInRequest request = new GoogleSignInRequest();
        String expectedEmail = "user@example.com";
        request.setEmail(expectedEmail);
        assertEquals(expectedEmail, request.getEmail(),
                "Le getter doit retourner la valeur définie par le setter pour email");
    }

    @Test
    void testDefaultsAreNull() {
        GoogleSignInRequest request = new GoogleSignInRequest();
        assertNull(request.getEmail(), "Le champ email doit être null par défaut");
    }

    @Test
    void testAllFieldsTogether() {
        GoogleSignInRequest request = new GoogleSignInRequest();
        String expectedEmail = "contact@boutic.com";
        request.setEmail(expectedEmail);

        assertEquals(expectedEmail, request.getEmail());
    }
}
