package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {

    @Test
    @DisplayName("Valeurs par défaut")
    void defaultValues() {
        Article a = new Article();
        assertEquals(1, a.getVisible());
        assertEquals(0, a.getCatid());
        assertEquals(0, a.getImgVisible());
        assertNull(a.getArtid());
        assertNull(a.getCustomId());
        assertNull(a.getNom());
        assertNull(a.getPrix());
        assertNull(a.getDescription());
        assertNull(a.getUnite());
        assertNull(a.getImage());
        assertNull(a.getCategorie());
    }

    @Test
    @DisplayName("Getters/Setters")
    void gettersSetters() {
        Article a = new Article();
        a.setArtid(101);
        a.setCustomId(12);
        a.setNom("Café");
        a.setPrix(3.5);
        a.setDescription("Moulu 250g");
        a.setVisible(0);
        a.setCatid(7);
        a.setUnite("paquet");
        a.setImage("img.png");
        a.setImgVisible(1);

        assertEquals(101, a.getArtid());
        assertEquals(12, a.getCustomId());
        assertEquals("Café", a.getNom());
        assertEquals(3.5, a.getPrix());
        assertEquals("Moulu 250g", a.getDescription());
        assertEquals(0, a.getVisible());
        assertEquals(7, a.getCatid());
        assertEquals("paquet", a.getUnite());
        assertEquals("img.png", a.getImage());
        assertEquals(1, a.getImgVisible());
    }

    @Test
    @DisplayName("Relation catégorie - setter/getter simple")
    void categorieRelation() {
        Article a = new Article();
        // Mock léger de Categorie via classe anonyme
        Categorie cat = new Categorie() {
            @Override
            public String getNom() { return "Boissons"; }
        };
        a.setCategorie(cat);
        assertNotNull(a.getCategorie());
        assertEquals("Boissons", a.getCategorie().getNom());
    }

    @Nested
    @DisplayName("getDisplayData()")
    class GetDisplayDataTests {
        @Test
        @DisplayName("Remplit correctement les champs avec catégorie")
        void displayData_withCategorie() {
            Article a = new Article();
            a.setArtid(5);
            a.setNom("Thé vert");
            a.setPrix(2.2);
            a.setDescription("Sachet 20");
            a.setVisible(1);
            a.setUnite("boîte");
            Categorie cat = new Categorie() {
                @Override
                public String getNom() { return "Boissons"; }
            };
            a.setCategorie(cat);

            List<Object> row = a.getDisplayData();
            assertEquals(7, row.size());
            assertEquals(5, row.get(0));
            assertEquals("Thé vert", row.get(1));
            assertEquals(2.2, row.get(2));
            assertEquals("Sachet 20", row.get(3));
            assertEquals("1", row.get(4)); // visible converti en string
            assertEquals("Boissons", row.get(5));
            assertEquals("boîte", row.get(6));
        }

        @Test
        @DisplayName("Catégorie null -> chaîne vide")
        void displayData_withoutCategorie() {
            Article a = new Article();
            a.setArtid(6);
            a.setNom("Sucre");
            a.setPrix(1.1);
            a.setDescription("Blond");
            a.setVisible(0);
            a.setUnite("kg");
            a.setCategorie(null);

            List<Object> row = a.getDisplayData();
            assertEquals(7, row.size());
            assertEquals(6, row.get(0));
            assertEquals("Sucre", row.get(1));
            assertEquals(1.1, row.get(2));
            assertEquals("Blond", row.get(3));
            assertEquals("0", row.get(4));
            assertEquals("", row.get(5)); // catégorie vide
            assertEquals("kg", row.get(6));
        }
    }
}