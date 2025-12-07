package com.ecommerce.praticboutic_backend_java.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

@Configuration
public class CorsConfig {

    private final ClientUrlsProperties clientUrlsProperties;

    @Autowired
    public CorsConfig(ClientUrlsProperties clientUrlsProperties) {
        this.clientUrlsProperties = clientUrlsProperties;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Autorise les cookies, headers d'authentification, etc.
        config.setAllowedOrigins(clientUrlsProperties.getClientUrls()); // Injection de la propriété ici
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept", "Accept-Language", "X-Authorization"));
        config.setExposedHeaders(Arrays.asList("Content-Length", "X-JSON"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setMaxAge(86400L); // 1 jour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Appliquer à toutes les routes

        return new CorsFilter(source);
    }
}
