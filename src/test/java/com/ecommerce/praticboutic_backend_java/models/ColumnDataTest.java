package com.ecommerce.praticboutic_backend_java.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColumnDataTest {

    private ColumnData columnData;

    @BeforeEach
    void setUp() {
        columnData = new ColumnData();
    }

    @Test
    void testNomGetterSetter() {
        columnData.setNom("id");
        assertEquals("id", columnData.getNom());
    }

    @Test
    void testDescGetterSetter() {
        columnData.setDesc("Identifiant unique");
        assertEquals("Identifiant unique", columnData.getDesc());
    }

    @Test
    void testTypeGetterSetter() {
        columnData.setType("INTEGER");
        assertEquals("INTEGER", columnData.getType());
    }

    @Test
    void testValeurGetterSetter() {
        columnData.setValeur("123");
        assertEquals("123", columnData.getValeur());
    }

    @Test
    void testAllPropertiesTogether() {
        columnData.setNom("name");
        columnData.setDesc("Nom de l'utilisateur");
        columnData.setType("VARCHAR");
        columnData.setValeur("John Doe");

        assertEquals("name", columnData.getNom());
        assertEquals("Nom de l'utilisateur", columnData.getDesc());
        assertEquals("VARCHAR", columnData.getType());
        assertEquals("John Doe", columnData.getValeur());
    }
}
