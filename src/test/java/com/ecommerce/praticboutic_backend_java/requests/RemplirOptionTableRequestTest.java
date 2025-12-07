package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemplirOptionTableRequestTest {

    @Test
    void testSetAndGetTable() {
        RemplirOptionTableRequest request = new RemplirOptionTableRequest();
        String expectedTable = "products";
        request.setTable(expectedTable);
        assertEquals(expectedTable, request.getTable(),
                "Le getter doit retourner la valeur définie par le setter pour table");
    }

    @Test
    void testSetAndGetBouticid() {
        RemplirOptionTableRequest request = new RemplirOptionTableRequest();
        Long expectedBouticid = 101L;
        request.setBouticid(expectedBouticid);
        assertEquals(expectedBouticid, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetColonne() {
        RemplirOptionTableRequest request = new RemplirOptionTableRequest();
        String expectedColonne = "price";
        request.setColonne(expectedColonne);
        assertEquals(expectedColonne, request.getColonne(),
                "Le getter doit retourner la valeur définie par le setter pour colonne");
    }

    @Test
    void testDefaultsAreNull() {
        RemplirOptionTableRequest request = new RemplirOptionTableRequest();
        assertAll(
                () -> assertNull(request.getTable(), "Le champ table doit être null par défaut"),
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getColonne(), "Le champ colonne doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        RemplirOptionTableRequest request = new RemplirOptionTableRequest();
        request.setTable("orders");
        request.setBouticid(202L);
        request.setColonne("quantity");

        assertAll(
                () -> assertEquals("orders", request.getTable()),
                () -> assertEquals(202L, request.getBouticid()),
                () -> assertEquals("quantity", request.getColonne())
        );
    }
}
