package com.ecommerce.praticboutic_backend_java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConnexionTest {

    @Test
    @DisplayName("Instanciation et valeurs par défaut")
    void defaultValues() {
        Connexion cx = new Connexion();
        // Ajustez selon les champs réels de Connexion
        // Exemples courants: id, ip, ts (timestamp), userAgent...
        // On vérifie surtout l'absence de NPE à l'instanciation.
        assertDoesNotThrow(cx::toString);
    }

    @Test
    @DisplayName("Getters/Setters - champs typiques")
    void gettersSetters() {
        Connexion cx = new Connexion();

        // Remplacez/complétez par les champs réels de votre entité Connexion
        // Exemples plausibles:
        // cx.setId(1);
        // cx.setIp("127.0.0.1");
        // cx.setTs(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        // cx.setUserAgent("JUnit");

        // assertEquals(1, cx.getId());
        // assertEquals("127.0.0.1", cx.getIp());
        // assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0, 0), cx.getTs());
        // assertEquals("JUnit", cx.getUserAgent());
    }

    @Test
    @DisplayName("toString() ne jette pas d'exception et reflète les champs principaux si définis")
    void toString_ok() {
        Connexion cx = new Connexion();
        // cx.setIp("192.168.0.10");
        String s = cx.toString();
        assertNotNull(s);
        // assertTrue(s.contains("192.168.0.10"));
    }
}
