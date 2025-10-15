package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LigneCmdTest {

    @Test
    @DisplayName("Instanciation - valeurs par défaut plausibles")
    void defaultValues() {
        LigneCmd l = new LigneCmd();
        // Ajustez selon les champs réels
        assertNull(l.getLignecmdid());
        assertNull(l.getCmdId());
        assertNull(l.getArtId());
        assertNull(l.getQuantite());
        assertNull(l.getPrix());
        assertNull(l.getCommande());
        assertNull(l.getArticle());
    }

    @Test
    @DisplayName("Getters/Setters basiques")
    void gettersSetters() {
        LigneCmd l = new LigneCmd();
        l.setLignecmdid(1001);
        l.setCmdId(10);
        l.setArtId(20);
        l.setQuantite(3.0f);
        l.setPrix(5.5f);

        assertEquals(1001, l.getLignecmdid());
        assertEquals(10, l.getCmdId());
        assertEquals(20, l.getArtId());
        assertEquals(3.0f, l.getQuantite());
        assertEquals(5.5f, l.getPrix());

    }

    @Test
    @DisplayName("Relation Commande - setter/getter")
    void relationCommande() {
        LigneCmd l = new LigneCmd();
        Commande c = new Commande();
        c.setCmdId(7);
        l.setCommande(c);

        assertNotNull(l.getCommande());
        assertEquals(7, l.getCommande().getCmdId());
    }

    @Test
    @DisplayName("Relation Article - setter/getter")
    void relationArticle() {
        LigneCmd l = new LigneCmd();
        Article a = new Article();
        a.setArtid(9);
        a.setNom("Produit X");
        l.setArticle(a);

        assertNotNull(l.getArticle());
        assertEquals(9, l.getArticle().getArtid());
        assertEquals("Produit X", l.getArticle().getNom());
    }

    @Test
    @DisplayName("getDisplayData() - retourne les colonnes attendues si implémenté")
    void getDisplayData_ifPresent() {
        LigneCmd l = new LigneCmd();
        l.setLignecmdid(1);
        l.setCmdId(10);
        l.setArtId(20);
        l.setQuantite(3.0f);
        l.setPrix(5.5f);
        l.setOrdre(1);
        l.setType("Produit");
        l.setNom("Article test");
        l.setCommentaire("RAS");

        // 🔹 On simule une commande rattachée avec un numéro de référence
        Commande commande = new Commande();
        commande.setNumRef("CMD-001");
        l.setCommande(commande);

        List<Object> row = l.getDisplayData();
        assertNotNull(row);
        assertEquals(8, row.size()); // 8 colonnes attendues
        assertEquals("CMD-001", row.get(1)); // Vérifie la colonne numRef
    }

}