package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    @DisplayName("Valeurs par défaut après instanciation")
    void defaultValues() {
        Customer c = new Customer();
        // Adaptez ces assertions aux champs réels de votre entité Customer
        assertNull(c.getCustomId());
        assertNull(c.getCustomer());
        assertNull(c.getActif());
        assertNull(c.getClient());
    }

    @Test
    @DisplayName("Getters/Setters basiques")
    void gettersSetters() {
        Customer c = new Customer();
        c.setCustomId(123);
        c.setCustomer("ma-boutique");
        c.setActif(1);

        assertEquals(123, c.getCustomId());
        assertEquals("ma-boutique", c.getCustomer());
        assertEquals(1, c.getActif());
    }

    @Test
    @DisplayName("Relation avec Client - setter/getter")
    void relationClient() {
        Customer c = new Customer();
        Client cli = new Client();
        cli.setCltId(10);
        c.setClient(cli);

        assertNotNull(c.getClient());
        assertEquals(10, c.getClient().getCltId());
    }

    @Test
    @DisplayName("toString() ne jette pas d'exception et reflète le customer si défini")
    void toString_ok() {
        Customer c = new Customer();
        c.setCustomer("boutique-x");
        String s = c.toString();
        assertNotNull(s);
        assertTrue(s.contains("boutique-x"));
    }
}