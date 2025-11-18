package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SuppressionRequestTest {

    @Test
    void testSetAndGetEmail() {
        SuppressionRequest request = new SuppressionRequest();
        String expectedEmail = "user@example.com";
        request.setEmail(expectedEmail);
        assertEquals(expectedEmail, request.getEmail(),
                "Le getter doit retourner la valeur définie par le setter pour email");
    }

    @Test
    void testSetAndGetBouticid() {
        SuppressionRequest request = new SuppressionRequest();
        Integer expectedBouticid = 101;
        request.setBouticid(expectedBouticid);
        assertEquals(expectedBouticid, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testDefaultsAreNull() {
        SuppressionRequest request = new SuppressionRequest();
        assertAll(
                () -> assertNull(request.getEmail(), "Le champ email doit être null par défaut"),
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        SuppressionRequest request = new SuppressionRequest();
        request.setEmail("admin@example.com");
        request.setBouticid(202);

        assertAll(
                () -> assertEquals("admin@example.com", request.getEmail()),
                () -> assertEquals(202, request.getBouticid())
        );
    }
}

