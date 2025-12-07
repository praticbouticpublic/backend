package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtListImgTest {

    @Test
    @DisplayName("Constructeur par défaut - champs null")
    void defaultConstructor() {
        ArtListImg img = new ArtListImg();
        assertNull(img.getArtListImgId());
        assertNull(img.getCustomid());
        assertNull(img.getArtid());
        assertNull(img.getImage());
        assertNull(img.getFavori());
        assertNull(img.getVisible());
    }

    @Test
    @DisplayName("Constructeur complet - initialise correctement les champs")
    void allArgsConstructor() {
        ArtListImg img = new ArtListImg(11, 22, "path.png", 1, 1);
        assertNull(img.getArtListImgId());
        assertEquals(11, img.getCustomid());
        assertEquals(22, img.getArtid());
        assertEquals("path.png", img.getImage());
        assertEquals(1, img.getFavori());
        assertEquals(1, img.getVisible());
    }

    @Test
    @DisplayName("Getters/Setters")
    void gettersSetters() {
        ArtListImg img = new ArtListImg();
        img.setCustomid(5);
        img.setArtId(7);
        img.setImage("img.jpg");
        img.setFavori(0);
        img.setVisible(1);

        assertEquals(5, img.getCustomid());
        assertEquals(7, img.getArtid());
        assertEquals("img.jpg", img.getImage());
        assertEquals(0, img.getFavori());
        assertEquals(1, img.getVisible());
    }

    @Test
    @DisplayName("setArtListImgId n'affecte pas la valeur (comportement actuel)")
    void setArtListImgId_noEffect_currentBehavior() {
        ArtListImg img = new ArtListImg();
        assertNull(img.getArtListImgId());

        img.setArtListImgId(123); // méthode actuelle réassigne this.artlistimgid à lui-même
        assertNull(img.getArtListImgId()); // reste null selon l'implémentation actuelle
    }

    @Test
    @DisplayName("toString contient tous les champs")
    void toStringContainsFields() {
        ArtListImg img = new ArtListImg(1, 2, "a.png", 1, 0);
        String s = img.toString();
        assertTrue(s.contains("ArtListImg{"));
        assertTrue(s.contains("customid=1"));
        assertTrue(s.contains("artid=2"));
        assertTrue(s.contains("image='a.png'"));
        assertTrue(s.contains("favori=1"));
        assertTrue(s.contains("visible=0"));
    }

    @Test
    @DisplayName("getDisplayData() retourne les colonnes attendues")
    void getDisplayData_ok() {
        ArtListImg img = new ArtListImg();
        img.setCustomid(9);
        img.setArtId(77);
        img.setImage("p.png");
        img.setFavori(1);
        img.setVisible(0);

        List<Object> row = img.getDisplayData();
        assertEquals(5, row.size());
        assertNull(row.get(0)); // id (null par défaut et setArtListImgId n'a pas d'effet)
        assertEquals(9, row.get(1)); // customid
        assertEquals("p.png", row.get(2)); // image
        assertEquals(1, row.get(3)); // favori
        assertEquals(0, row.get(4)); // visible
    }
}