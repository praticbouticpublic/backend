package com.ecommerce.praticboutic_backend_java.requests;

import com.ecommerce.praticboutic_backend_java.models.ColumnData;

import java.util.List;

import com.ecommerce.praticboutic_backend_java.models.ColumnData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateRowRequestTest {

    @Test
    void testSetAndGetTable() {
        UpdateRowRequest request = new UpdateRowRequest();
        String expectedTable = "products";
        request.setTable(expectedTable);
        assertEquals(expectedTable, request.getTable(),
                "Le getter doit retourner la valeur définie par le setter pour table");
    }

    @Test
    void testSetAndGetBouticid() {
        UpdateRowRequest request = new UpdateRowRequest();
        Long expectedBouticid = 101L;
        request.setBouticid(expectedBouticid);
        assertEquals(expectedBouticid, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetRow() {
        UpdateRowRequest request = new UpdateRowRequest();
        List<ColumnData> expectedRow = new ArrayList<>();
        expectedRow.add(new ColumnData());
        request.setRow(expectedRow);
        assertEquals(expectedRow, request.getRow(),
                "Le getter doit retourner la valeur définie par le setter pour row");
    }

    @Test
    void testSetAndGetIdtoup() {
        UpdateRowRequest request = new UpdateRowRequest();
        Long expectedIdtoup = 202L;
        request.setIdtoup(expectedIdtoup);
        assertEquals(expectedIdtoup, request.getIdtoup(),
                "Le getter doit retourner la valeur définie par le setter pour idtoup");
    }

    @Test
    void testSetAndGetColonne() {
        UpdateRowRequest request = new UpdateRowRequest();
        String expectedColonne = "price";
        request.setColonne(expectedColonne);
        assertEquals(expectedColonne, request.getColonne(),
                "Le getter doit retourner la valeur définie par le setter pour colonne");
    }

    @Test
    void testDefaultsAreNull() {
        UpdateRowRequest request = new UpdateRowRequest();
        assertAll(
                () -> assertNull(request.getTable(), "Le champ table doit être null par défaut"),
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut"),
                () -> assertNull(request.getRow(), "Le champ row doit être null par défaut"),
                () -> assertNull(request.getIdtoup(), "Le champ idtoup doit être null par défaut"),
                () -> assertNull(request.getColonne(), "Le champ colonne doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        UpdateRowRequest request = new UpdateRowRequest();
        request.setTable("orders");
        request.setBouticid(303L);
        List<ColumnData> row = new ArrayList<>();
        row.add(new ColumnData());
        request.setRow(row);
        request.setIdtoup(404L);
        request.setColonne("status");

        assertAll(
                () -> assertEquals("orders", request.getTable()),
                () -> assertEquals(303L, request.getBouticid()),
                () -> assertEquals(row, request.getRow()),
                () -> assertEquals(404L, request.getIdtoup()),
                () -> assertEquals("status", request.getColonne())
        );
    }
}
