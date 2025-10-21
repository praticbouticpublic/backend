package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandeTest {

    @Test
    @DisplayName("Valeurs par défaut après instanciation")
    void defaultValues() {
        Commande c = new Commande();
        assertNull(c.getCmdId());
        assertNull(c.getCustomId());
        assertNull(c.getNumRef());
        assertNull(c.getNom());
        assertNull(c.getPrenom());
        assertNull(c.getTelephone());
        assertNull(c.getAdresse1());
        assertNull(c.getAdresse2());
        assertNull(c.getCodePostal());
        assertNull(c.getVille());
        assertNull(c.getVente());
        assertNull(c.getPaiement());
        assertEquals(0.0, c.getSsTotal());
        assertEquals(0.0, c.getRemise());
        assertEquals(0.0, c.getFraisLivraison());
        assertEquals(0.0, c.getTotal());
        assertNull(c.getCommentaire());
        assertNull(c.getMethod());
        assertNull(c.getTable());
        assertNull(c.getDateCreation());
        assertNull(c.getStatId());
        assertNull(c.getStatutCmd());
    }

    @Test
    @DisplayName("Getters/Setters de base")
    void gettersSetters() {
        Commande c = new Commande();
        c.setCmdId(1);
        c.setCustomId(2);
        c.setNumRef("REF-001");
        c.setNom("Doe");
        c.setPrenom("John");
        c.setTelephone("0102030405");
        c.setAdresse1("12 rue A");
        c.setAdresse2("Bat B");
        c.setCodePostal("75001");
        c.setVille("Paris");
        c.setVente("en ligne");
        c.setPaiement("CB");
        c.setSsTotal(100.5);
        c.setRemise(10.0);
        c.setFraisLivraison(4.9);
        c.setTotal(95.4);
        c.setCommentaire("RAS");
        c.setMethod("delivery");
        c.setTable(7);
        LocalDateTime now = LocalDateTime.of(2023, 3, 15, 14, 5, 9);
        c.setDateCreation(now);
        c.setStatId(3);

        assertEquals(1, c.getCmdId());
        assertEquals(2, c.getCustomId());
        assertEquals("REF-001", c.getNumRef());
        assertEquals("Doe", c.getNom());
        assertEquals("John", c.getPrenom());
        assertEquals("0102030405", c.getTelephone());
        assertEquals("12 rue A", c.getAdresse1());
        assertEquals("Bat B", c.getAdresse2());
        assertEquals("75001", c.getCodePostal());
        assertEquals("Paris", c.getVille());
        assertEquals("en ligne", c.getVente());
        assertEquals("CB", c.getPaiement());
        assertEquals(100.5, c.getSsTotal());
        assertEquals(10.0, c.getRemise());
        assertEquals(4.9, c.getFraisLivraison());
        assertEquals(95.4, c.getTotal());
        assertEquals("RAS", c.getCommentaire());
        assertEquals("delivery", c.getMethod());
        assertEquals(7, c.getTable());
        assertEquals(now, c.getDateCreation());
        assertEquals(3, c.getStatId());
    }

    @Test
    @DisplayName("getDisplayData() - formatte la date et utilise le libellé du statut")
    void getDisplayData_formatsDateAndUsesStatus() {
        Commande c = new Commande();
        c.setCmdId(10);
        c.setNumRef("X-100");
        c.setNom("Martin");
        c.setPrenom("Alice");
        c.setTelephone("0600000000");
        c.setAdresse1("1 rue de la Paix");
        c.setAdresse2("Etage 2");
        c.setCodePostal("33000");
        c.setVille("Bordeaux");
        c.setVente("magasin");
        c.setPaiement("espèces");
        c.setSsTotal(50.0);
        c.setRemise(5.0);
        c.setFraisLivraison(0.0);
        c.setTotal(45.0);
        c.setCommentaire("Urgent");
        c.setMethod("pickup");
        c.setTable(12);
        // Date spécifique pour valider le format "dd MMMM yyyy, HH:mm:ss" en français
        c.setDateCreation(LocalDateTime.of(2024, 1, 5, 9, 7, 3));

        // Mock simple de StatutCmd pour retourner un état
        StatutCmd statut = new StatutCmd() {
            @Override
            public String getEtat() { return "EN_PREPARATION"; }
        };
        c.setStatutCmd(statut);

        List<Object> row = c.getDisplayData();
        assertEquals(20, row.size());
        assertEquals(10, row.get(0));                // cmdid
        assertEquals("X-100", row.get(1));           // numref
        assertEquals("Martin", row.get(2));          // nom
        assertEquals("Alice", row.get(3));           // prenom
        assertEquals("0600000000", row.get(4));      // telephone
        assertEquals("1 rue de la Paix", row.get(5));// adresse1
        assertEquals("Etage 2", row.get(6));         // adresse2
        assertEquals("33000", row.get(7));           // codepostal
        assertEquals("Bordeaux", row.get(8));        // ville
        assertEquals("magasin", row.get(9));         // vente
        assertEquals("espèces", row.get(10));        // paiement
        assertEquals(50.0, row.get(11));             // sstotal
        assertEquals(5.0, row.get(12));              // remise
        assertEquals(0.0, row.get(13));              // fraislivraison
        assertEquals(45.0, row.get(14));             // total
        assertEquals("Urgent", row.get(15));         // commentaire
        assertEquals("pickup", row.get(16));         // method
        assertEquals(12, row.get(17));               // table

        // Vérifie le format de date exact en français
        assertEquals("05 janvier 2024, 09:07:03", row.get(18));

        // Statut via getEtat()
        assertEquals("EN_PREPARATION", row.get(19));
    }

    @Test
    @DisplayName("getDisplayData() - NPE si date/statut null (comportement actuel)")
    void getDisplayData_nullDateOrStatus_currentBehavior() {
        Commande c = new Commande();
        // Sans setDateCreation ni setStatutCmd, l'appel devrait lancer un NPE avec l'implémentation actuelle
        assertThrows(NullPointerException.class, c::getDisplayData);
    }
}