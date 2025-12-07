package com.ecommerce.praticboutic_backend_java.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;


@Import(SecurityConfig.class)
class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void passwordEncoder_works() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);

        String raw = "123456";
        String encoded = encoder.encode(raw);

        assertTrue(encoder.matches(raw, encoded));
    }
}
