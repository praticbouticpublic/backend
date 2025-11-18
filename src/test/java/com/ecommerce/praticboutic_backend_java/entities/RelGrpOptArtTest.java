package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RelGrpOptArtTest {

    private RelGrpOptArt relGrpOptArt;
    private GroupeOpt mockGroupeOpt;
    private Article mockArticle;

    @BeforeEach
    void setUp() {
        relGrpOptArt = new RelGrpOptArt();
        relGrpOptArt.setRelgrpoartid(1);
        relGrpOptArt.setCustomId(100);
        relGrpOptArt.setGrpoptid(10);
        relGrpOptArt.setArtid(20);
        relGrpOptArt.setVisible(1);

        // Création d’objets simulés (stubs)
        mockGroupeOpt = new GroupeOpt();
        mockGroupeOpt.setNom("Groupe Test");

        mockArticle = new Article();
        mockArticle.setNom("Article Test");

        relGrpOptArt.setGroupeOpt(mockGroupeOpt);
        relGrpOptArt.setArticle(mockArticle);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1, relGrpOptArt.getRelgrpoartid());
        assertEquals(100, relGrpOptArt.getCustomId());
        assertEquals(10, relGrpOptArt.getGrpoptid());
        assertEquals(20, relGrpOptArt.getArtid());
        assertEquals(1, relGrpOptArt.getVisible());
    }

    @Test
    void testIsVisibleWhenVisibleIs1() {
        relGrpOptArt.setVisible(1);
        assertTrue(relGrpOptArt.isVisible());
    }

    @Test
    void testIsVisibleWhenVisibleIs0() {
        relGrpOptArt.setVisible(0);
        assertFalse(relGrpOptArt.isVisible());
    }

    @Test
    void testIsVisibleWhenVisibleIsNull() {
        relGrpOptArt.setVisible(null);
        assertFalse(relGrpOptArt.isVisible());
    }

    @Test
    void testGetDisplayData() {
        List<Object> data = relGrpOptArt.getDisplayData();

        assertNotNull(data);
        assertEquals(4, data.size());
        assertEquals(1, data.get(0));                   // relgrpoartid
        assertEquals("Groupe Test", data.get(1));       // nom groupe
        assertEquals("Article Test", data.get(2));      // nom article
        assertEquals("1", data.get(3));                 // visible
    }

    @Test
    void testGetDisplayDataWithNullRelations() {
        relGrpOptArt.setGroupeOpt(null);
        relGrpOptArt.setArticle(null);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            relGrpOptArt.getDisplayData();
        });

        assertNotNull(exception);
    }
}
