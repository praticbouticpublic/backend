package com.ecommerce.praticboutic_backend_java.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "allowed.client")
public class ClientUrlsProperties {

    private List<String> clientUrls;

    public List<String> getClientUrls() {
        return clientUrls;
    }

    public void setClientUrls(List<String> clientUrls) {
        this.clientUrls = clientUrls;
    }
}
