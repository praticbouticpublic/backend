package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParametreTest {

    @Test
    @DisplayName("Valeurs par défaut après instanciation")
    void defaultValues() {
        Parametre p = new Parametre();
        // paramid n'a pas de getter exposé dans l'entité fournie
        assertNull(p.getCustomid());
        // Les champs suivants n'ont pas de getters dans l'extrait fourni;
        // si présents dans votre classe, dé-commentez et adaptez:
        // assertNull(p.getNom());
        // assertNull(p.getValeur());
        // assertNull(p.getCommentaire());
    }

    @Test
    @DisplayName("Constructeur complet initialise correctement les champs")
    void allArgsConstructor() {
        Parametre p = new Parametre(10, "SIZE_IMG", "smallimg", "taille d'image");
        assertEquals(10, p.getCustomid());
        // Si des getters existent, vérifiez-les également:
        // assertEquals("SIZE_IMG", p.getNom());
        // assertEquals("smallimg", p.getValeur());
        // assertEquals("taille d'image", p.getCommentaire());
    }

    @Test
    @DisplayName("Getter/Setter customid")
    void getterSetterCustomid() {
        Parametre p = new Parametre();
        p.setCustomid(42);
        assertEquals(42, p.getCustomid());
    }
}