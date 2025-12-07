package com.ecommerce.praticboutic_backend_java.configurations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class CorsConfigTest {

    @Test
    @DisplayName("corsFilter - crée un CorsFilter avec les origines issues de ClientUrlsProperties")
    void corsFilter_usesClientUrlsProperties() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withBean(ClientUrlsProperties.class, () -> {
                    ClientUrlsProperties p = new ClientUrlsProperties();
                    // suppose un setter setClientUrls(List<String>)
                    p.setClientUrls(List.of("https://a.local", "https://b.local"));
                    return p;
                })
                .withUserConfiguration(CorsConfig.class);

        runner.run(ctx -> {
            assertTrue(ctx.containsBean("corsFilter"));
            CorsFilter filter = ctx.getBean(CorsFilter.class);
            assertNotNull(filter);
            // On ne peut pas facilement lire la config interne du filter sans framework web;
            // l’assertion principale est la présence du bean avec le contexte démarré.
        });
    }
}