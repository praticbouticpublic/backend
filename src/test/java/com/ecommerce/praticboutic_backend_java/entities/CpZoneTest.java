package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CpZoneTest {

    @Test
    @DisplayName("Valeurs par défaut")
    void defaultValues() {
        CpZone z = new CpZone();
        // cpzoneid: pas de getter dédié exposé, vérification via displayData
        assertNull(z.getCustomid());
        assertNull(z.getCodepostal());
        assertNull(z.getVille());
        assertEquals(1, z.getActif());
    }

    @Test
    @DisplayName("Getters/Setters")
    void gettersSetters() {
        CpZone z = new CpZone();
        z.setCustomid(12);
        z.setCodepostal("33000");
        z.setVille("Bordeaux");
        z.setActif(0);

        assertEquals(12, z.getCustomid());
        assertEquals("33000", z.getCodepostal());
        assertEquals("Bordeaux", z.getVille());
        assertEquals(0, z.getActif());
    }

    @Test
    @DisplayName("getDisplayData() retourne les colonnes attendues")
    void getDisplayData_ok() {
        CpZone z = new CpZone();
        z.setCustomid(34);
        z.setCodepostal("75001");
        z.setVille("Paris");
        z.setActif(1);

        List<Object> row = z.getDisplayData();
        assertEquals(4, row.size());
        assertNull(row.get(0));          // id non défini (pas de setter exposé)
        assertEquals("75001", row.get(1));
        assertEquals("Paris", row.get(2));
        assertEquals("1", row.get(3));   // actif converti en String
    }
}