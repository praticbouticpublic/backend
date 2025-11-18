package com.ecommerce.praticboutic_backend_java.requests;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemiseRequestTest {

    @Test
    void testSetAndGetFields() {
        RemiseRequest request = new RemiseRequest();

        request.customer = "user123";
        request.sstotal = 150.75;
        request.code = "PROMO20";

        assertAll(
                () -> assertEquals("user123", request.customer),
                () -> assertEquals(150.75, request.sstotal),
                () -> assertEquals("PROMO20", request.code)
        );
    }

    @Test
    void testDefaultsAreNullOrZero() {
        RemiseRequest request = new RemiseRequest();

        assertAll(
                () -> assertNull(request.customer, "Le champ customer doit être null par défaut"),
                () -> assertEquals(0.0, request.sstotal, "Le champ sstotal doit être 0.0 par défaut"),
                () -> assertNull(request.code, "Le champ code doit être null par défaut")
        );
    }
}
