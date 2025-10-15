package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategorieTest {

    @Test
    @DisplayName("Valeurs par défaut")
    void defaultValues() {
        Categorie c = new Categorie();
        // Adaptez ces attentes selon les champs réels de Categorie
        assertNull(c.getCatid());
        assertNull(c.getNom());
        // Si Categorie possède un champ 'visible' par défaut par exemple:
        // assertEquals(1, c.getVisible());
    }

    @Test
    @DisplayName("Getters/Setters")
    void gettersSetters() {
        Categorie c = new Categorie();
        c.setCatid(7);
        c.setNom("Boissons");

        assertEquals(7, c.getCatid());
        assertEquals("Boissons", c.getNom());
    }

    @Test
    @DisplayName("toString() - ne jette pas d'exception et inclut le nom si défini")
    void toString_ok() {
        Categorie c = new Categorie();
        c.setNom("Épicerie");
        String s = c.toString();
        assertNotNull(s);
        assertTrue(s.contains("Épicerie"));
    }

    @Test
    @DisplayName("Compatibilité avec Article.getDisplayData()")
    void worksWithArticleDisplayData() {
        Categorie cat = new Categorie();
        cat.setNom("Snacks");

        Article a = new Article();
        a.setArtid(1);
        a.setNom("Chips");
        a.setPrix(1.99);
        a.setDescription("Sel");
        a.setVisible(1);
        a.setUnite("paquet");
        a.setCategorie(cat);

        var row = a.getDisplayData();
        assertEquals("Snacks", row.get(5));
    }
}