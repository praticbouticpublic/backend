package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testSetAndGetEmail() {
        LoginRequest request = new LoginRequest();
        String expectedEmail = "user@example.com";
        request.setEmail(expectedEmail);
        assertEquals(expectedEmail, request.getEmail(),
                "Le getter doit retourner la valeur définie par le setter pour email");
    }

    @Test
    void testSetAndGetPassword() {
        LoginRequest request = new LoginRequest();
        String expectedPassword = "securePassword123";
        request.setPassword(expectedPassword);
        assertEquals(expectedPassword, request.getPassword(),
                "Le getter doit retourner la valeur définie par le setter pour password");
    }

    @Test
    void testDefaultsAreNull() {
        LoginRequest request = new LoginRequest();
        assertAll(
                () -> assertNull(request.getEmail(), "Le champ email doit être null par défaut"),
                () -> assertNull(request.getPassword(), "Le champ password doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@example.com");
        request.setPassword("admin123");

        assertAll(
                () -> assertEquals("admin@example.com", request.getEmail()),
                () -> assertEquals("admin123", request.getPassword())
        );
    }
}
