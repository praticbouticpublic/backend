package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupeOptTest {

    @Test
    @DisplayName("Valeurs par défaut")
    void defaultValues() {
        GroupeOpt g = new GroupeOpt();
        assertNull(g.getGrpoptid());
        assertNull(g.getCustomId());
        assertNull(g.getNom());
        assertEquals(1, g.getVisible());
        assertEquals(1, g.getMultiple()); // d'après l'implémentation actuelle
    }

    @Test
    @DisplayName("Getters/Setters")
    void gettersSetters() {
        GroupeOpt g = new GroupeOpt();
        g.setGrpoptid(101);
        g.setCustomId(12);
        g.setNom("Tailles");
        g.setVisible(0);
        g.setMultiple(0);

        assertEquals(101, g.getGrpoptid());
        assertEquals(12, g.getCustomId());
        assertEquals("Tailles", g.getNom());
        assertEquals(0, g.getVisible());
        assertEquals(0, g.getMultiple());
    }

    @Test
    @DisplayName("getDisplayData() - colonnes attendues et conversions en String")
    void getDisplayData_ok() {
        GroupeOpt g = new GroupeOpt();
        g.setGrpoptid(7);
        g.setNom("Sauces");
        g.setVisible(1);
        g.setMultiple(0);

        List<Object> row = g.getDisplayData();
        assertEquals(4, row.size());
        assertEquals(7, row.get(0));       // id
        assertEquals("Sauces", row.get(1));// nom
        assertEquals("1", row.get(2));     // visible -> String
        assertEquals("0", row.get(3));     // multiple -> String
    }
}
