package com.ecommerce.praticboutic_backend_java.configurations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class EmailConfigTest {

    private EmailConfig emailConfig;

    @BeforeEach
    void setUp() {
        emailConfig = new EmailConfig();
        // Injection simple des @Value via réflexion pour le test unitaire pur
        setField(emailConfig, "host", "smtp.example.com");
        setField(emailConfig, "port", 587);
        setField(emailConfig, "username", "user");
        setField(emailConfig, "password", "pass");
        setField(emailConfig, "auth", true);
        setField(emailConfig, "starttls", true);
        setField(emailConfig, "debug", false);
    }

    @Test
    @DisplayName("getJavaMailSender - retourne un JavaMailSender configuré")
    void getJavaMailSender_returnsConfiguredSender() {
        JavaMailSender sender = emailConfig.getJavaMailSender();
        assertNotNull(sender);
        assertTrue(sender instanceof JavaMailSenderImpl);

        JavaMailSenderImpl impl = (JavaMailSenderImpl) sender;
        assertEquals("smtp.example.com", impl.getHost());
        assertEquals(587, impl.getPort());
        assertEquals("user", impl.getUsername());
        assertEquals("pass", impl.getPassword());

        Properties props = impl.getJavaMailProperties();
        assertEquals("smtp", props.getProperty("mail.transport.protocol"));
        assertEquals("true", props.getProperty("mail.smtp.auth"));
        assertEquals("true", props.getProperty("mail.smtp.starttls.enable"));
        assertEquals("false", props.getProperty("mail.debug"));
        assertEquals("smtp.example.com", props.getProperty("mail.smtp.ssl.trust"));
        assertEquals("TLSv1.2", props.getProperty("mail.smtp.ssl.protocols"));
    }

    // Utilitaire de test pour setter les champs @Value
    private static void setField(Object target, String name, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("Impossible d'injecter le champ " + name + ": " + e.getMessage());
        }
    }
}