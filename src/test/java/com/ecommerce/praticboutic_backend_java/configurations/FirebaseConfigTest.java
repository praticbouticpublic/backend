package com.ecommerce.praticboutic_backend_java.configurations;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FirebaseConfigTest {

    private FirebaseConfig config = new FirebaseConfig();

    @AfterEach
    void tearDown() {
        // Supprime toutes les apps Firebase pour isoler les tests
        List<FirebaseApp> apps = FirebaseApp.getApps();
        for (FirebaseApp app : apps) {
            app.delete();
        }
    }

    @Test
    @DisplayName("firebaseApp - retourne un mock FirebaseApp")
    void firebaseApp_initializesWithMock() throws Exception {
        // Création du mock FirebaseApp et FirebaseOptions
        FirebaseApp mockApp = mock(FirebaseApp.class);
        FirebaseOptions mockOptions = mock(FirebaseOptions.class);
        when(mockApp.getOptions()).thenReturn(mockOptions);

        // Spy de la config pour remplacer firebaseApp() par notre mock
        FirebaseConfig spyConfig = spy(config);
        doReturn(mockApp).when(spyConfig).firebaseApp();

        FirebaseApp app = spyConfig.firebaseApp();

        assertNotNull(app);
        assertSame(mockApp, app);
        assertSame(mockOptions, app.getOptions());
    }

    @Test
    @DisplayName("firebaseMessaging - retourne une instance mockée")
    void firebaseMessaging_returnsInstance() throws Exception {
        FirebaseApp mockApp = mock(FirebaseApp.class);
        FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);

        try (MockedStatic<FirebaseMessaging> mocked = mockStatic(FirebaseMessaging.class)) {
            mocked.when(() -> FirebaseMessaging.getInstance(mockApp)).thenReturn(mockMessaging);

            FirebaseMessaging messaging = config.firebaseMessaging(mockApp);

            assertNotNull(messaging);
            assertSame(mockMessaging, messaging);
        }
    }

    @Test
    @DisplayName("firebaseApp - lève IOException si le flux est null")
    void firebaseApp_throwsWhenStreamNull() {
        config.setServiceAccountSupplier(() -> null);

        Exception ex = assertThrows(Exception.class, config::firebaseApp);
        assertTrue(ex.getMessage().contains("Firebase Service Account key not found."));
    }
}
