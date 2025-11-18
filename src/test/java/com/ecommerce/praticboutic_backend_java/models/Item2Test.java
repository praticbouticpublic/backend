package com.ecommerce.praticboutic_backend_java.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Item2Test {

    private Item2 item;

    @BeforeEach
    void setUp() {
        item = new Item2();
    }

    @Test
    void testIdGetterSetter() {
        item.setId("123");
        assertEquals("123", item.getId());
    }

    @Test
    void testNameGetterSetter() {
        item.setName("Produit A");
        assertEquals("Produit A", item.getName());
    }

    @Test
    void testTypeGetterSetter() {
        item.setType("Type1");
        assertEquals("Type1", item.getType());
    }

    @Test
    void testPrixGetterSetter() {
        item.setPrix(12.5);
        assertEquals(12.5, item.getPrix());
    }

    @Test
    void testQtGetterSetter() {
        item.setQt(3);
        assertEquals(3, item.getQt());
    }

    @Test
    void testOptsGetterSetter() {
        item.setOpts("Option1,Option2");
        assertEquals("Option1,Option2", item.getOpts());
    }

    @Test
    void testUniteGetterSetter() {
        item.setUnite("kg");
        assertEquals("kg", item.getUnite());
    }

    @Test
    void testTxtaGetterSetter() {
        item.setTxta("Description produit");
        assertEquals("Description produit", item.getTxta());
    }

    @Test
    void testAllPropertiesTogether() {
        item.setId("001");
        item.setName("Produit B");
        item.setType("Type2");
        item.setPrix(25.0);
        item.setQt(5);
        item.setOpts("OptA,OptB");
        item.setUnite("pcs");
        item.setTxta("Texte additionnel");

        assertEquals("001", item.getId());
        assertEquals("Produit B", item.getName());
        assertEquals("Type2", item.getType());
        assertEquals(25.0, item.getPrix());
        assertEquals(5, item.getQt());
        assertEquals("OptA,OptB", item.getOpts());
        assertEquals("pcs", item.getUnite());
        assertEquals("Texte additionnel", item.getTxta());
    }
}
