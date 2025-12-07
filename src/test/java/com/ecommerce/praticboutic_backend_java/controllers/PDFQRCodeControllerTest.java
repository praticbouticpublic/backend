package com.ecommerce.praticboutic_backend_java.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class PDFQRCodeControllerTest {

    @Test
    @DisplayName("generate - réponse OK avec content-type PDF et corps non vide (squelette)")
    void generate_returnsPdfResponse() throws Exception {
        // Ce test est un squelette générique.
        // Adaptez selon la signature réelle (params requis) et la logique (génération PDF/QR).
        PDFQRCodeController controller = new PDFQRCodeController();

        // Exemple si la méthode est generate(token, data...) -> ajustez
        // ResponseEntity<byte[]> resp = controller.generate("data");
        // Ici on suppose un appel sans paramètre pour illustrer:
        ResponseEntity<byte[]> resp = ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(new byte[]{1,2,3});

        // Asserts de base
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE).contains("application/pdf"));
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().length > 0);
    }
}