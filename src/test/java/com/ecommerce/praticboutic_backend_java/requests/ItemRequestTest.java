package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void testSetAndGetId() {
        Item item = new Item();
        String expected = "item1";
        item.setId(expected);
        assertEquals(expected, item.getId());
    }

    @Test
    void testSetAndGetType() {
        Item item = new Item();
        String expected = "product";
        item.setType(expected);
        assertEquals(expected, item.getType());
    }

    @Test
    void testSetAndGetName() {
        Item item = new Item();
        String expected = "Produit A";
        item.setName(expected);
        assertEquals(expected, item.getName());
    }

    @Test
    void testSetAndGetPrix() {
        Item item = new Item();
        Double expected = 12.5;
        item.setPrix(expected);
        assertEquals(expected, item.getPrix());
    }

    @Test
    void testSetAndGetQt() {
        Item item = new Item();
        Integer expected = 3;
        item.setQt(expected);
        assertEquals(expected, item.getQt());
    }

    @Test
    void testSetAndGetUnite() {
        Item item = new Item();
        String expected = "kg";
        item.setUnite(expected);
        assertEquals(expected, item.getUnite());
    }

    @Test
    void testSetAndGetOpts() {
        Item item = new Item();
        String expected = "option1,option2";
        item.setOpts(expected);
        assertEquals(expected, item.getOpts());
    }

    @Test
    void testSetAndGetTxta() {
        Item item = new Item();
        String expected = "note supplémentaire";
        item.setTxta(expected);
        assertEquals(expected, item.getTxta());
    }

    @Test
    void testDefaultsAreNull() {
        Item item = new Item();
        assertAll(
                () -> assertNull(item.getId()),
                () -> assertNull(item.getType()),
                () -> assertNull(item.getName()),
                () -> assertNull(item.getPrix()),
                () -> assertNull(item.getQt()),
                () -> assertNull(item.getUnite()),
                () -> assertNull(item.getOpts()),
                () -> assertNull(item.getTxta())
        );
    }

    @Test
    void testAllFieldsTogether() {
        Item item = new Item();
        item.setId("item123");
        item.setType("service");
        item.setName("Service X");
        item.setPrix(99.99);
        item.setQt(5);
        item.setUnite("unit");
        item.setOpts("optA,optB");
        item.setTxta("note");

        assertAll(
                () -> assertEquals("item123", item.getId()),
                () -> assertEquals("service", item.getType()),
                () -> assertEquals("Service X", item.getName()),
                () -> assertEquals(99.99, item.getPrix()),
                () -> assertEquals(5, item.getQt()),
                () -> assertEquals("unit", item.getUnite()),
                () -> assertEquals("optA,optB", item.getOpts()),
                () -> assertEquals("note", item.getTxta())
        );
    }
}
