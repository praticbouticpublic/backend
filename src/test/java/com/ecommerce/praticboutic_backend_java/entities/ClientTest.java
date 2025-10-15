package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    @DisplayName("Constructeur par défaut et valeurs initiales")
    void defaultConstructor_values() {
        Client c = new Client();
        assertNull(c.getCltId());
        assertNull(c.getEmail());
        assertNull(c.getPass());
        assertNull(c.getQualite());
        assertNull(c.getNom());
        assertNull(c.getPrenom());
        assertNull(c.getAdr1());
        assertNull(c.getAdr2());
        assertNull(c.getCp());
        assertNull(c.getVille());
        assertNull(c.getTel());
        assertNull(c.getStripeCustomerId());
        assertNull(c.isActif());
        assertNull(c.getDevice_id());
        assertNull(c.getDevice_type());
        assertNull(c.getCustomers());
        assertFalse(c.isPresent());
    }

    @Test
    @DisplayName("Constructeur complet - initialise tous les champs fournis")
    void fullArgsConstructor() {
        Client c = new Client(
                "john@example.com", "pwd", "M.", "Doe", "John",
                "rue A", "appt 1", "75001", "Paris", "0102030405",
                "cus_123", 1, "dev123", "android"
        );
        assertEquals("john@example.com", c.getEmail());
        assertEquals("pwd", c.getPass());
        assertEquals("M.", c.getQualite());
        assertEquals("Doe", c.getNom());
        assertEquals("John", c.getPrenom());
        assertEquals("rue A", c.getAdr1());
        assertEquals("appt 1", c.getAdr2());
        assertEquals("75001", c.getCp());
        assertEquals("Paris", c.getVille());
        assertEquals("0102030405", c.getTel());
        assertEquals("cus_123", c.getStripeCustomerId());
        assertEquals(1, c.isActif());
        assertEquals("dev123", c.getDevice_id());
        assertEquals("android", c.getDevice_type());
    }

    @Test
    @DisplayName("Constructeur (email, pass, nom, prenom) - actif=1 par défaut")
    void constructorEssential_activeDefault() {
        Client c = new Client("a@b.c", "pwd", "Nom", "Prenom");
        assertEquals("a@b.c", c.getEmail());
        assertEquals("pwd", c.getPass());
        assertEquals("Nom", c.getNom());
        assertEquals("Prenom", c.getPrenom());
        assertEquals(1, c.isActif());
    }

    @Test
    @DisplayName("Constructeur (email, pass, qualite, nom, prenom) - actif=1 par défaut")
    void constructorWithQualite_activeDefault() {
        Client c = new Client("a@b.c", "pwd", "M.", "Nom", "Prenom");
        assertEquals("M.", c.getQualite());
        assertEquals(1, c.isActif());
    }

    @Test
    @DisplayName("Getters/Setters - champs simples")
    void gettersSetters() {
        Client c = new Client();
        c.setCltId(10);
        c.setEmail("x@y.z");
        c.setPass("secret");
        c.setQualite("Mme");
        c.setNom("Durand");
        c.setPrenom("Anne");
        c.setAdr1("12 rue X");
        c.setAdr2("Bat B");
        c.setCp("33000");
        c.setVille("Bordeaux");
        c.setTel("0600000000");
        c.setStripeCustomerId("cus_999");
        c.setActif(0);
        c.setDevice_id("dev-1");
        c.setDevice_type("ios");

        assertEquals(10, c.getCltId());
        assertEquals("x@y.z", c.getEmail());
        assertEquals("secret", c.getPass());
        assertEquals("Mme", c.getQualite());
        assertEquals("Durand", c.getNom());
        assertEquals("Anne", c.getPrenom());
        assertEquals("12 rue X", c.getAdr1());
        assertEquals("Bat B", c.getAdr2());
        assertEquals("33000", c.getCp());
        assertEquals("Bordeaux", c.getVille());
        assertEquals("0600000000", c.getTel());
        assertEquals("cus_999", c.getStripeCustomerId());
        assertEquals(0, c.isActif());
        assertEquals("dev-1", c.getDevice_id());
        assertEquals("ios", c.getDevice_type());
    }

    @Nested
    @DisplayName("isPresent()")
    class IsPresentTests {
        @Test
        void idNull_false() {
            Client c = new Client();
            c.setCltId(null);
            assertFalse(c.isPresent());
        }
        @Test
        void idZero_false() {
            Client c = new Client();
            c.setCltId(0);
            assertFalse(c.isPresent());
        }
        @Test
        void idNonZero_true() {
            Client c = new Client();
            c.setCltId(5);
            assertTrue(c.isPresent());
        }
    }

    @Test
    @DisplayName("Relation clients secondaires (Customer) - set/get liste")
    void customersRelation_setGet() {
        Client c = new Client();
        var list = new ArrayList<Customer>();
        c.setCustomers(list);
        assertSame(list, c.getCustomers());
    }
}