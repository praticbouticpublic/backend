package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VueTableRequestTest {

    @Test
    void testSetAndGetTable() {
        VueTableRequest request = new VueTableRequest();
        String expected = "products";
        request.setTable(expected);
        assertEquals(expected, request.getTable(),
                "Le getter doit retourner la valeur définie par le setter pour table");
    }

    @Test
    void testSetAndGetBouticid() {
        VueTableRequest request = new VueTableRequest();
        Integer expected = 101;
        request.setBouticid(expected);
        assertEquals(expected, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetSelcol() {
        VueTableRequest request = new VueTableRequest();
        String expected = "id";
        request.setSelcol(expected);
        assertEquals(expected, request.getSelcol(),
                "Le getter doit retourner la valeur définie par le setter pour selcol");
    }

    @Test
    void testSetAndGetSelid() {
        VueTableRequest request = new VueTableRequest();
        Integer expected = 5;
        request.setSelid(expected);
        assertEquals(expected, request.getSelid(),
                "Le getter doit retourner la valeur définie par le setter pour selid");
    }

    @Test
    void testSetAndGetLimite() {
        VueTableRequest request = new VueTableRequest();
        Integer expected = 50;
        request.setLimite(expected);
        assertEquals(expected, request.getLimite(),
                "Le getter doit retourner la valeur définie par le setter pour limite");
    }

    @Test
    void testSetAndGetOffset() {
        VueTableRequest request = new VueTableRequest();
        Integer expected = 10;
        request.setOffset(expected);
        assertEquals(expected, request.getOffset(),
                "Le getter doit retourner la valeur définie par le setter pour offset");
    }

    @Test
    void testDefaultsAreNull() {
        VueTableRequest request = new VueTableRequest();
        assertAll(
                () -> assertNull(request.getTable(), "Le champ table doit être null par défaut"),
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getSelcol(), "Le champ selcol doit être null par défaut"),
                () -> assertNull(request.getSelid(), "Le champ selid doit être null par défaut"),
                () -> assertNull(request.getLimite(), "Le champ limite doit être null par défaut"),
                () -> assertNull(request.getOffset(), "Le champ offset doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        VueTableRequest request = new VueTableRequest();
        request.setTable("orders");
        request.setBouticid(202);
        request.setSelcol("customer_id");
        request.setSelid(15);
        request.setLimite(100);
        request.setOffset(5);

        assertAll(
                () -> assertEquals("orders", request.getTable()),
                () -> assertEquals(202, request.getBouticid()),
                () -> assertEquals("customer_id", request.getSelcol()),
                () -> assertEquals(15, request.getSelid()),
                () -> assertEquals(100, request.getLimite()),
                () -> assertEquals(5, request.getOffset())
        );
    }
}

