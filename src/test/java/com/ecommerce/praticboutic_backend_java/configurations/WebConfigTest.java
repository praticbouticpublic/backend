package com.ecommerce.praticboutic_backend_java.configurations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class WebConfigTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(WebConfig.class);

    @Test
    @DisplayName("WebConfig est chargé dans le contexte et implémente WebMvcConfigurer")
    void webConfig_loaded_and_isWebMvcConfigurer() {
        contextRunner.run(ctx -> {
            assertTrue(ctx.containsBean("webConfig"));
            Object bean = ctx.getBean("webConfig");
            assertNotNull(bean);
            assertInstanceOf(WebMvcConfigurer.class, bean);
        });
    }
}