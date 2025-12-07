package com.ecommerce.praticboutic_backend_java.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientUrlsProperties)) return false;
        ClientUrlsProperties that = (ClientUrlsProperties) o;
        return Objects.equals(clientUrls, that.clientUrls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientUrls);
    }

    @Override
    public String toString() {
        return "ClientUrlsProperties{clientUrls=" + clientUrls + '}';
    }
}
