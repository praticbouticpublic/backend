package com.ecommerce.praticboutic_backend_java.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

    /**
     * Chemin vers le fichier JSON du compte de service.
     * Exemple : classpath:firebase-service-account.json
     */
    private static Resource serviceAccount;

    public static Resource getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(Resource serviceAccount) {
        FirebaseProperties.serviceAccount = serviceAccount;
    }
}
