package com.ecommerce.praticboutic_backend_java.configurations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    @DisplayName("passwordEncoder - bean présent et BCrypt fonctionnel")
    void passwordEncoder_bean_present_and_works() {
        assertNotNull(passwordEncoder);
        String hash = passwordEncoder.encode("secret");
        assertTrue(passwordEncoder.matches("secret", hash));
    }

    @Test
    @DisplayName("securityFilterChain - bean présent et construit sans erreur")
    void securityFilterChain_bean_present() {
        assertNotNull(securityFilterChain);
    }
}
