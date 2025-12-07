package com.ecommerce.praticboutic_backend_java.entities;
import com.ecommerce.praticboutic_backend_java.entities.BarLivr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BarLivrTest {

    @Test
    @DisplayName("Setters/Getters - id")
    void id_setter_getter() {
        BarLivr b = new BarLivr();
        assertNull(b.getBarLivrid());
        b.setBarLivrid(42);
        assertEquals(42, b.getBarLivrid());
    }

    @Test
    @DisplayName("Setters/Getters - champs principaux")
    void setters_getters_mainFields() {
        BarLivr b = new BarLivr();

        // Ajustez ces setters/getters aux champs réels de BarLivr si différents
        b.setValminin(0.0F);
        b.setValmaxex(1000F);
        b.setActif(1);
        b.setCustomId(10);

        assertEquals(0.0f, b.getValminin());
        assertEquals(1000F, b.getValmaxex());
        assertEquals(1, b.getActif());
        assertEquals(10, b.getCustomId());
    }

    @Test
    @DisplayName("Valeurs par défaut plausibles")
    void default_values() {
        BarLivr b = new BarLivr();
        assertNull(b.getBarLivrid());
        // Ajustez selon l'implémentation réelle (peut-être 1 ou 0, ou null)
        // Ici on vérifie simplement que l'objet est instanciable sans NPE.
        assertDoesNotThrow(b::toString);
    }
}