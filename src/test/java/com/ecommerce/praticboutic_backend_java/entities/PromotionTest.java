package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PromotionTest {

    private Promotion promotion;

    @BeforeEach
    void setUp() {
        promotion = new Promotion();
        promotion.setPromoid(1);
        promotion.setCustomid(100);
        promotion.setCode("PROMO10");
        promotion.setTaux(10.0);
        promotion.setActif(1);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1, promotion.getPromoid());
        assertEquals(100, promotion.getCustomid());
        assertEquals("PROMO10", promotion.getCode());
        assertEquals(10.0, promotion.getTaux());
        assertEquals(1, promotion.getActif());
    }

    @Test
    void testSettersUpdateValues() {
        promotion.setPromoid(2);
        promotion.setCustomid(200);
        promotion.setCode("PROMO20");
        promotion.setTaux(20.0);
        promotion.setActif(0);

        assertEquals(2, promotion.getPromoid());
        assertEquals(200, promotion.getCustomid());
        assertEquals("PROMO20", promotion.getCode());
        assertEquals(20.0, promotion.getTaux());
        assertEquals(0, promotion.getActif());
    }

    @Test
    void testGetDisplayData() {
        List<Object> displayData = promotion.getDisplayData();

        assertNotNull(displayData);
        assertEquals(4, displayData.size());
        assertEquals(1, displayData.get(0));              // promoid
        assertEquals("PROMO10", displayData.get(1));      // code
        assertEquals(10.0, displayData.get(2));           // taux
        assertEquals("1", displayData.get(3));            // actif as String
    }

    @Test
    void testDefaultActifValue() {
        Promotion promo = new Promotion();
        assertEquals(1, promo.getActif()); // par défaut actif = 1
    }
}
