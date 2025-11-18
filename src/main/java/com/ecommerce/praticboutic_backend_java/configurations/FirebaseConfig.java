package com.ecommerce.praticboutic_backend_java.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.fail;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.serviceaccount.ressource.key}")
    private String JsonKey;

    // Hook test-only: fournisseur de flux substituable en test
    private Supplier<InputStream> serviceAccountSupplier;
    private Supplier<FirebaseApp> firebaseAppSupplier;


    // package-private pour tests
    void setServiceAccountSupplier(Supplier<InputStream> supplier) {
        this.serviceAccountSupplier = supplier;
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount;

        if (serviceAccountSupplier != null) {
            serviceAccount = serviceAccountSupplier.get(); // flux injecté pour tests
        } else {
            serviceAccount = getClass().getClassLoader().getResourceAsStream(JsonKey); // production
        }

        if (serviceAccount == null) {
            throw new IOException("Firebase Service Account key not found.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }


    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }


    private FirebaseProperties firebaseProperties = new FirebaseProperties();


    // ... existing code ...
// Utilitaire d’injection reflexive d’un champ privé
    private static void setField(Object target, String name, Object value) {
        try {
            Class<?> c = target.getClass();
            Field f = null;
            while (c != null) {
                try {
                    f = c.getDeclaredField(name);
                    break;
                } catch (NoSuchFieldException ignore) {
                    c = c.getSuperclass();
                }
            }
            if (f == null) {
                fail("Impossible d'injecter le champ " + name + ": champ introuvable");
                return;
            }
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("Impossible d'injecter le champ " + name + ": " + e.getMessage());
        }
    }


    void setFirebaseAppSupplier(Supplier<FirebaseApp> supplier) {
        this.firebaseAppSupplier = supplier;
    }

    public FirebaseProperties getFirebaseProperties() {
        return firebaseProperties;
    }

    public void setFirebaseProperties(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }
}