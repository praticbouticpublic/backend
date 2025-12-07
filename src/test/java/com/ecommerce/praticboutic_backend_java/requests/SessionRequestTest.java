package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionRequestTest {

    @Test
    void testSetAndGetSessionId() {
        SessionRequest request = new SessionRequest();
        String expectedSessionId = "ABC123XYZ";
        request.setSessionId(expectedSessionId);
        assertEquals(expectedSessionId, request.getSessionId(),
                "Le getter doit retourner la valeur définie par le setter pour sessionId");
    }

    @Test
    void testDefaultIsNull() {
        SessionRequest request = new SessionRequest();
        assertNull(request.getSessionId(), "Le champ sessionId doit être null par défaut");
    }
}
