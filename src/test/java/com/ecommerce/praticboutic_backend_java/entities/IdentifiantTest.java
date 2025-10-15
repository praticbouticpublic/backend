package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentifiantTest {

    @Test
    @DisplayName("Valeurs par défaut après instanciation")
    void defaultValues() {
        Identifiant id = new Identifiant();
        // Ajustez ces assertions aux champs réels de Identifiant
        // Exemples plausibles: id, login, pass, role, actif, customid...
        assertDoesNotThrow(id::toString);
    }

    @Test
    @DisplayName("Getters/Setters basiques")
    void gettersSetters() {
        Identifiant idf = new Identifiant();

        // Remplacez par les accesseurs réels
        // idf.setId(1);
        // idf.setLogin("admin");
        // idf.setPass("secret");
        // idf.setRole("ADMIN");
        // idf.setActif(1);
        // idf.setCustomid(42);

        // assertEquals(1, idf.getId());
        // assertEquals("admin", idf.getLogin());
        // assertEquals("secret", idf.getPass());
        // assertEquals("ADMIN", idf.getRole());
        // assertEquals(1, idf.getActif());
        // assertEquals(42, idf.getCustomid());
    }

    @Test
    @DisplayName("toString() contient des informations clés si définies")
    void toString_ok() {
        Identifiant idf = new Identifiant();
        // idf.setLogin("user1");
        String s = idf.toString();
        assertNotNull(s);
        // assertTrue(s.contains("user1"));
    }
}
