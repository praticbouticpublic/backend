package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SendCodeRequestTest {

    @Test
    void testSetAndGetEmail() {
        SendCodeRequest request = new SendCodeRequest();
        String expectedEmail = "user@example.com";
        request.setEmail(expectedEmail);
        assertEquals(expectedEmail, request.getEmail(),
                "Le getter doit retourner la valeur définie par le setter pour email");
    }

    @Test
    void testDefaultIsNull() {
        SendCodeRequest request = new SendCodeRequest();
        assertNull(request.getEmail(), "Le champ email doit être null par défaut");
    }
}
