package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginLinkRequestTest {

    @Test
    void testSetAndGetBouticid() {
        LoginLinkRequest request = new LoginLinkRequest();
        Integer expectedBouticid = 123;
        request.setBouticid(expectedBouticid);
        assertEquals(expectedBouticid, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetPlatform() {
        LoginLinkRequest request = new LoginLinkRequest();
        String expectedPlatform = "iOS";
        request.setPlatform(expectedPlatform);
        assertEquals(expectedPlatform, request.getPlatform(),
                "Le getter doit retourner la valeur définie par le setter pour platform");
    }

    @Test
    void testDefaultsAreNull() {
        LoginLinkRequest request = new LoginLinkRequest();
        assertAll(
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getPlatform(), "Le champ platform doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        LoginLinkRequest request = new LoginLinkRequest();
        request.setBouticid(456);
        request.setPlatform("Android");

        assertAll(
                () -> assertEquals(456, request.getBouticid()),
                () -> assertEquals("Android", request.getPlatform())
        );
    }
}
