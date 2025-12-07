package com.ecommerce.praticboutic_backend_java.requests;

import com.ecommerce.praticboutic_backend_java.models.ColumnData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InsertRowRequestTest {

    @Test
    void testSetAndGetTable() {
        InsertRowRequest request = new InsertRowRequest();
        String expectedTable = "products";
        request.setTable(expectedTable);
        assertEquals(expectedTable, request.getTable(),
                "Le getter doit retourner la valeur définie par le setter pour table");
    }

    @Test
    void testSetAndGetBouticid() {
        InsertRowRequest request = new InsertRowRequest();
        Long expectedBouticid = 101L;
        request.setBouticid(expectedBouticid);
        assertEquals(expectedBouticid, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetRow() {
        InsertRowRequest request = new InsertRowRequest();
        ColumnData col1 = new ColumnData();
        ColumnData col2 = new ColumnData();
        List<ColumnData> expectedRow = List.of(col1, col2);

        request.setRow(expectedRow);

        assertNotNull(request.getRow());
        assertEquals(2, request.getRow().size());
        assertEquals(col1, request.getRow().get(0));
        assertEquals(col2, request.getRow().get(1));
    }

    @Test
    void testDefaultsAreNull() {
        InsertRowRequest request = new InsertRowRequest();
        assertAll(
                () -> assertNull(request.getTable(), "Le champ table doit être null par défaut"),
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getRow(), "Le champ row doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        InsertRowRequest request = new InsertRowRequest();
        ColumnData col = new ColumnData();
        List<ColumnData> rowList = List.of(col);

        request.setTable("orders");
        request.setBouticid(202L);
        request.setRow(rowList);

        assertAll(
                () -> assertEquals("orders", request.getTable()),
                () -> assertEquals(202L, request.getBouticid()),
                () -> assertEquals(rowList, request.getRow())
        );
    }
}


