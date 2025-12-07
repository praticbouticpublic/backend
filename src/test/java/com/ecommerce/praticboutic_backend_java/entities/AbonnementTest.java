package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AbonnementTest {

    @Test
    @DisplayName("Getters/Setters de base")
    void testGettersSetters() {
        Abonnement abo = new Abonnement();
        abo.setAboId(10);
        abo.setCltId(20);
        abo.setCreationBoutic(true);  // booléen
        abo.setBouticId(30);
        abo.setStripeSubscriptionId("sub_123");
        abo.setActif(1);              // int
        abo.setMetered(0);            // int

        assertEquals(10, abo.getAboId());
        assertEquals(20, abo.getCltId());
        assertTrue(abo.aBoutiqueCreee());  // booléen
        assertEquals(30, abo.getBouticId());
        assertEquals("sub_123", abo.getStripeSubscriptionId());
        assertEquals(1, abo.getActif());
        assertEquals(0, abo.getMetered());
    }

    @Nested
    @DisplayName("isActif()")
    class IsActifTests {
        @Test
        void actifNull_retourFalse() {
            Abonnement abo = new Abonnement();
            abo.setActif(null);
            assertFalse(abo.isActif());
        }

        @Test
        void actifZero_retourFalse() {
            Abonnement abo = new Abonnement();
            abo.setActif(0);
            assertFalse(abo.isActif());
        }

        @Test
        void actifUn_retourTrue() {
            Abonnement abo = new Abonnement();
            abo.setActif(1);
            assertTrue(abo.isActif());
        }

        @Test
        void actifAutreValeur_retourFalse() {
            Abonnement abo = new Abonnement();
            abo.setActif(2);
            assertFalse(abo.isActif());
        }
    }

    @Nested
    @DisplayName("isMetered()")
    class IsMeteredTests {
        @Test
        void meteredNull_retourFalse() {
            Abonnement abo = new Abonnement();
            abo.setMetered(null);
            assertFalse(abo.isMetered());
        }

        @Test
        void meteredZero_retourFalse() {
            Abonnement abo = new Abonnement();
            abo.setMetered(0);
            assertFalse(abo.isMetered());
        }

        @Test
        void meteredUn_retourTrue() {
            Abonnement abo = new Abonnement();
            abo.setMetered(1);
            assertTrue(abo.isMetered());
        }

        @Test
        void meteredAutreValeur_retourFalse() {
            Abonnement abo = new Abonnement();
            abo.setMetered(3);
            assertFalse(abo.isMetered());
        }
    }

    @Nested
    @DisplayName("aBoutiqueCreee()")
    class ABoutiqueCreeeTests {
        @Test
        void creationBouticNull_retourFalse() {
            Abonnement abo = new Abonnement();
            abo.setCreationBoutic(null);
            assertFalse(abo.aBoutiqueCreee());
        }

        @Test
        void creationBouticFalse_retourFalse() {
            Abonnement abo = new Abonnement();
            abo.setCreationBoutic(false);
            assertFalse(abo.aBoutiqueCreee());
        }

        @Test
        void creationBouticTrue_retourTrue() {
            Abonnement abo = new Abonnement();
            abo.setCreationBoutic(true);
            assertTrue(abo.aBoutiqueCreee());
        }
    }

    @Test
    @DisplayName("setTypePlan/setDateDebut/setDateFin ne lèvent pas d'erreur (méthodes vides)")
    void stubbedMethods_doNotThrow() {
        Abonnement abo = new Abonnement();
        assertDoesNotThrow(() -> {
            abo.setTypePlan("premium");
            abo.setDateDebut(LocalDate.now());
            abo.setDateFin(LocalDate.now().plusDays(30));
        });
    }
}
