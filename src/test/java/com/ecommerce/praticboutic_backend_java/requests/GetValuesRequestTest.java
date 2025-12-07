package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GetValuesRequestTest {

    @Test
    void testSetAndGetTable() {
        GetValuesRequest request = new GetValuesRequest();
        String expectedTable = "products";
        request.setTable(expectedTable);
        assertEquals(expectedTable, request.getTable(),
                "Le getter doit retourner la valeur définie par le setter pour table");
    }

    @Test
    void testSetAndGetBouticid() {
        GetValuesRequest request = new GetValuesRequest();
        Long expectedBouticid = 101L;
        request.setBouticid(expectedBouticid);
        assertEquals(expectedBouticid, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetIdtoup() {
        GetValuesRequest request = new GetValuesRequest();
        Long expectedIdtoup = 202L;
        request.setIdtoup(expectedIdtoup);
        assertEquals(expectedIdtoup, request.getIdtoup(),
                "Le getter doit retourner la valeur définie par le setter pour idtoup");
    }

    @Test
    void testDefaultsAreNull() {
        GetValuesRequest request = new GetValuesRequest();
        assertAll(
                () -> assertNull(request.getTable(), "Le champ table doit être null par défaut"),
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getIdtoup(), "Le champ idtoup doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        GetValuesRequest request = new GetValuesRequest();
        String expectedTable = "categories";
        Long expectedBouticid = 1L;
        Long expectedIdtoup = 2L;

        request.setTable(expectedTable);
        request.setBouticid(expectedBouticid);
        request.setIdtoup(expectedIdtoup);

        assertAll(
                () -> assertEquals(expectedTable, request.getTable()),
                () -> assertEquals(expectedBouticid, request.getBouticid()),
                () -> assertEquals(expectedIdtoup, request.getIdtoup())
        );
    }
}
