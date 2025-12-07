package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    @Test
    @DisplayName("Valeurs par défaut après instanciation")
    void defaultValues() {
        Image img = new Image();
        // Adaptez aux champs réels de votre entité Image
        assertNull(img.getId());
        assertNull(img.getCustomId());
        assertNull(img.getArtid());
        assertNull(img.getImage());
        // Si l’entité possède des flags par défaut (visible, favori...), ajustez ici:
        // assertEquals(1, img.getVisible());
    }

    @Test
    @DisplayName("Getters/Setters basiques (inclut le champ image)")
    void gettersSetters() {
        Image img = new Image();
        img.setId(10);
        img.setCustomId(2);
        img.setArtid(50);
        img.setImage("path/to/img.png");

        assertEquals(10, img.getId());
        assertEquals(2, img.getCustomId());
        assertEquals(50, img.getArtid());
        assertEquals("path/to/img.png", img.getImage());
    }

    @Test
    @DisplayName("toString() ne jette pas d'exception et reflète l'image si définie")
    void toString_ok() {
        Image img = new Image();
        img.setImage("logo.png");
        String s = img.toString();
        assertNotNull(s);
        assertTrue(s.contains("logo.png"));
    }
}