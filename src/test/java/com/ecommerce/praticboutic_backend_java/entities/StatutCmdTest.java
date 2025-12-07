package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatutCmdTest {

    private StatutCmd statutCmd;

    @BeforeEach
    void setUp() {
        statutCmd = new StatutCmd(
                100,
                "En préparation",
                "#FFA500",
                "Votre commande est en préparation",
                0,
                1
        );
        statutCmd.setStatid(1);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, statutCmd.getStatid());
        assertEquals(100, statutCmd.getCustomId());
        assertEquals("En préparation", statutCmd.getEtat());
        assertEquals("#FFA500", statutCmd.getCouleur());
        assertEquals("Votre commande est en préparation", statutCmd.getMessage());
    }

    @Test
    void testSetters() {
        statutCmd.setStatid(2);
        statutCmd.setCustomId(200);
        statutCmd.setEtat("Livré");
        statutCmd.setCouleur("#00FF00");
        statutCmd.setMessage("Votre commande a été livrée");
        statutCmd.setDefaut(1);
        statutCmd.setActif(0);

        assertEquals(2, statutCmd.getStatid());
        assertEquals(200, statutCmd.getCustomId());
        assertEquals("Livré", statutCmd.getEtat());
        assertEquals("#00FF00", statutCmd.getCouleur());
        assertEquals("Votre commande a été livrée", statutCmd.getMessage());
    }

    @Test
    void testCommandesRelation() {
        Commande commande1 = new Commande();
        Commande commande2 = new Commande();
        List<Commande> commandes = new ArrayList<>();
        commandes.add(commande1);
        commandes.add(commande2);

        statutCmd.setComandes(commandes);

        assertNotNull(statutCmd.getCommandes());
        assertEquals(2, statutCmd.getCommandes().size());
        assertTrue(statutCmd.getCommandes().contains(commande1));
        assertTrue(statutCmd.getCommandes().contains(commande2));
    }

    @Test
    void testEmptyConstructor() {
        StatutCmd empty = new StatutCmd();
        assertNull(empty.getStatid());
        assertNull(empty.getCustomId());
        assertNull(empty.getEtat());
        assertNull(empty.getCouleur());
        assertNull(empty.getMessage());
        assertNull(empty.getCommandes());
    }
}
