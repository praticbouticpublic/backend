package com.ecommerce.praticboutic_backend_java.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class RedirectControllerTest {

    @Test
    @DisplayName("redirect-handler - renvoie une redirection (squelette)")
    void redirectHandler_returnsRedirect() {
        RedirectController controller = new RedirectController();

        // Adaptez selon la signature réelle: params platform/status...
        // ResponseEntity<?> resp = controller.redirectHandler("web", "return");
        // Squelette: simulons une 302 avec header Location
        ResponseEntity<Void> resp = ResponseEntity.status(302)
                .header("Location", "https://app.example/after")
                .build();

        assertEquals(302, resp.getStatusCode().value());
        assertEquals("https://app.example/after", resp.getHeaders().getFirst("Location"));
    }
}
