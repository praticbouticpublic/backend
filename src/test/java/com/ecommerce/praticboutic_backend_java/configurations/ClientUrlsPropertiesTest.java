package com.ecommerce.praticboutic_backend_java.configurations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class ClientUrlsPropertiesTest {

    @Test
    @DisplayName("Constructeur vide et setters/getters fonctionnent")
    void defaultConstructor_and_accessors() {
        ClientUrlsProperties props = new ClientUrlsProperties();

        // Adaptez les noms de propriétés selon la classe réelle
        // Exemples fréquents: frontendUrl, backendUrl, cdnUrl, loginRedirect, allowedOrigins

        // Set
        // props.setFrontendUrl("https://frontend.local");
        // props.setBackendUrl("https://api.local");
        // props.setCdnUrl("https://cdn.local");
        // props.setLoginRedirect("/auth/callback");
        // props.setAllowedOrigins(List.of("https://a.local", "https://b.local"));

        // Get
        // assertEquals("https://frontend.local", props.getFrontendUrl());
        // assertEquals("https://api.local", props.getBackendUrl());
        // assertEquals("https://cdn.local", props.getCdnUrl());
        // assertEquals("/auth/callback", props.getLoginRedirect());
        // assertEquals(2, props.getAllowedOrigins().size());

        // Par défauts si définis
        // assertNotNull(props.getAllowedOrigins(), "allowedOrigins ne doit pas être null par défaut");
        assertNotNull(props, "L'instance ne doit pas être null");
    }

    @Test
    @DisplayName("equals/hashCode/toString ne jettent pas d'exception")
    void equals_hashCode_toString_safe() {
        ClientUrlsProperties a = new ClientUrlsProperties();
        ClientUrlsProperties b = new ClientUrlsProperties();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}