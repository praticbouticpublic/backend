package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LiensRequestTest {

    @Test
    void testSetAndGetAction() {
        LiensRequest request = new LiensRequest();
        String expectedAction = "connect";
        request.setAction(expectedAction);
        assertEquals(expectedAction, request.getAction(),
                "Le getter doit retourner la valeur définie par le setter pour action");
    }

    @Test
    void testSetAndGetLogin() {
        LiensRequest request = new LiensRequest();
        String expectedLogin = "user123";
        request.setLogin(expectedLogin);
        assertEquals(expectedLogin, request.getLogin(),
                "Le getter doit retourner la valeur définie par le setter pour login");
    }

    @Test
    void testDefaultsAreNull() {
        LiensRequest request = new LiensRequest();
        assertAll(
                () -> assertNull(request.getAction(), "Le champ action doit être null par défaut"),
                () -> assertNull(request.getLogin(), "Le champ login doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        LiensRequest request = new LiensRequest();
        request.setAction("disconnect");
        request.setLogin("admin");

        assertAll(
                () -> assertEquals("disconnect", request.getAction()),
                () -> assertEquals("admin", request.getLogin())
        );
    }
}
