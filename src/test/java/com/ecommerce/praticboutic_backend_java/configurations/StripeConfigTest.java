package com.ecommerce.praticboutic_backend_java.configurations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class StripeConfigTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withPropertyValues(
                            "stripe.public.key=pk_test_456")
                    .withUserConfiguration(StripeConfig.class);

    @Test
    @DisplayName("Beans StripeConfig présents avec clés injectées")
    void stripeConfig_beans_present_withKeys() {
        contextRunner.run(ctx -> {
            assertTrue(ctx.containsBean("stripeConfig"));
            StripeConfig config = ctx.getBean(StripeConfig.class);
            assertNotNull(config);

            // Accès via getters
            assertEquals("pk_test_456", config.getPublicKey());
        });
    }
}
