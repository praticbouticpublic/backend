package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SMSRequestTest {

    @Test
    void testSetAndGetBouticid() {
        SMSRequest request = new SMSRequest();
        Integer expectedBouticid = 101;
        request.setBouticid(expectedBouticid);
        assertEquals(expectedBouticid, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetMessage() {
        SMSRequest request = new SMSRequest();
        String expectedMessage = "Hello World";
        request.setMessage(expectedMessage);
        assertEquals(expectedMessage, request.getMessage(),
                "Le getter doit retourner la valeur définie par le setter pour message");
    }

    @Test
    void testSetAndGetTelephone() {
        SMSRequest request = new SMSRequest();
        String expectedTelephone = "0123456789";
        request.setTelephone(expectedTelephone);
        assertEquals(expectedTelephone, request.getTelephone(),
                "Le getter doit retourner la valeur définie par le setter pour telephone");
    }

    @Test
    void testDefaultsAreNull() {
        SMSRequest request = new SMSRequest();
        assertAll(
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getMessage(), "Le champ message doit être null par défaut"),
                () -> assertNull(request.getTelephone(), "Le champ telephone doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        SMSRequest request = new SMSRequest();
        request.setBouticid(202);
        request.setMessage("Test message");
        request.setTelephone("0987654321");

        assertAll(
                () -> assertEquals(202, request.getBouticid()),
                () -> assertEquals("Test message", request.getMessage()),
                () -> assertEquals("0987654321", request.getTelephone())
        );
    }
}
