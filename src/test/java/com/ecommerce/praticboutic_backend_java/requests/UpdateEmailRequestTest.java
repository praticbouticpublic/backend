package com.ecommerce.praticboutic_backend_java.requests;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateEmailRequestTest {

    @Test
    void testSetAndGetEmail() {
        UpdateEmailRequest request = new UpdateEmailRequest();
        String expectedEmail = "user@example.com";
        request.setEmail(expectedEmail);
        assertEquals(expectedEmail, request.getEmail(),
                "Le getter doit retourner la valeur définie par le setter pour email");
    }

    @Test
    void testDefaultIsNull() {
        UpdateEmailRequest request = new UpdateEmailRequest();
        assertNull(request.getEmail(), "Le champ email doit être null par défaut");
    }
}
