package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShopConfigRequestTest {

    @Test
    void testSetAndGetChxmethode() {
        ShopConfigRequest request = new ShopConfigRequest();
        String expected = "delivery";
        request.setChxmethode(expected);
        assertEquals(expected, request.getChxmethode(),
                "Le getter doit retourner la valeur définie par le setter pour chxmethode");
    }

    @Test
    void testSetAndGetChxpaie() {
        ShopConfigRequest request = new ShopConfigRequest();
        String expected = "card";
        request.setChxpaie(expected);
        assertEquals(expected, request.getChxpaie(),
                "Le getter doit retourner la valeur définie par le setter pour chxpaie");
    }

    @Test
    void testSetAndGetMntmincmd() {
        ShopConfigRequest request = new ShopConfigRequest();
        String expected = "50";
        request.setMntmincmd(expected);
        assertEquals(expected, request.getMntmincmd(),
                "Le getter doit retourner la valeur définie par le setter pour mntmincmd");
    }

    @Test
    void testSetAndGetValidsms() {
        ShopConfigRequest request = new ShopConfigRequest();
        Integer expected = 15;
        request.setValidsms(expected);
        assertEquals(expected, request.getValidsms(),
                "Le getter doit retourner la valeur définie par le setter pour validsms");
    }

    @Test
    void testDefaultsAreNull() {
        ShopConfigRequest request = new ShopConfigRequest();
        assertAll(
                () -> assertNull(request.getChxmethode(), "Le champ chxmethode doit être null par défaut"),
                () -> assertNull(request.getChxpaie(), "Le champ chxpaie doit être null par défaut"),
                () -> assertNull(request.getMntmincmd(), "Le champ mntmincmd doit être null par défaut"),
                () -> assertNull(request.getValidsms(), "Le champ validsms doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        ShopConfigRequest request = new ShopConfigRequest();
        request.setChxmethode("pickup");
        request.setChxpaie("paypal");
        request.setMntmincmd("100");
        request.setValidsms(30);

        assertAll(
                () -> assertEquals("pickup", request.getChxmethode()),
                () -> assertEquals("paypal", request.getChxpaie()),
                () -> assertEquals("100", request.getMntmincmd()),
                () -> assertEquals(30, request.getValidsms())
        );
    }
}
