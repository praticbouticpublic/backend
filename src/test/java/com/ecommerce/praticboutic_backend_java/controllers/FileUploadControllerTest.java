package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class FileUploadControllerTest {

    private FileUploadController controller;

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        controller = new FileUploadController();
        tempDir = Files.createTempDirectory("upload-test-");
        inject(controller, "uploadDirectory", tempDir.toString());
        inject(controller, "maxFileSize", 5 * 1024 * 1024L); // 5MB
    }

    @Test
    @DisplayName("uploadFile - 401 si email non vérifié")
    void uploadFile_unauthorized_whenEmailNotVerified() {
        MockMultipartFile file = new MockMultipartFile("file", "a.jpg", "image/jpeg", new byte[]{1,2,3});
        Map<String, Object> payload = new HashMap<>(); // pas de verify_email

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.uploadFile(file, "Bearer tok");

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Courriel non vérifié"));
        }
    }

    @Test
    @DisplayName("uploadFile - 404 si pas de fichier")
    void uploadFile_notFound_whenNoFile() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.uploadFile(null, "Bearer tok");

            assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
            assertEquals("Pas de fichier", resp.getBody());
        }
    }

    @Test
    @DisplayName("uploadFile - 200 et retourne nom généré")
    void uploadFile_happyPath() {
        MockMultipartFile file = new MockMultipartFile("file", "a.jpg", "image/jpeg", new byte[]{1,2,3});
        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));
            jwtStatic.when(() -> JwtService.generateToken(anyMap(), anyString())).thenReturn("new.jwt");

            ResponseEntity<?> resp = controller.uploadFile(file, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertTrue(body.containsKey("result"));
            assertTrue(body.containsKey("token"));
            assertTrue(Files.exists(tempDir.resolve(((String) body.get("result")))));
        }
    }

    @Test
    @DisplayName("uploadFile - 400 si extension invalide")
    void uploadFile_invalidExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "script.exe", "application/octet-stream", new byte[]{1});
        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.uploadFile(file, "Bearer tok");

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("png, gif, jpg, jpeg"));
        }
    }

    @Test
    @DisplayName("uploadFile - 400 si mime invalide")
    void uploadFile_invalidMime() {
        MockMultipartFile file = new MockMultipartFile("file", "a.jpg", "application/octet-stream", new byte[]{1});
        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.uploadFile(file, "Bearer tok");

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("Type mime non reconnu"));
        }
    }

    @Test
    @DisplayName("uploadFile - 400 si trop gros")
    void uploadFile_tooLarge() {
        byte[] big = new byte[6 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile("file", "a.jpg", "image/jpeg", big);
        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok")).thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.uploadFile(file, "Bearer tok");

            assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            assertTrue(((Map<?, ?>) resp.getBody()).get("error").toString().contains("trop gros"));
        }
    }

    @Test
    @DisplayName("uploadFiles - 200 [] si aucun fichier")
    void uploadFiles_noFiles() {
        ResponseEntity<?> resp = controller.uploadFiles(null);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(((List<?>) resp.getBody()).isEmpty());
    }

    @Test
    @DisplayName("uploadFiles - 200 quand fichiers valides")
    void uploadFiles_happyPath() {
        MultipartFile f1 = new MockMultipartFile("file[]", "a.jpg", "image/jpeg", new byte[]{1});
        MultipartFile f2 = new MockMultipartFile("file[]", "b.png", "image/png", new byte[]{2});

        ResponseEntity<?> resp = controller.uploadFiles(new MultipartFile[]{f1, f2});

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        List<?> names = (List<?>) resp.getBody();
        assertEquals(2, names.size());
        assertTrue(Files.exists(tempDir.resolve((String) names.get(0))));
        assertTrue(Files.exists(tempDir.resolve((String) names.get(1))));
    }

    private static void inject(Object target, String field, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("Injection échouée: " + field + " - " + e.getMessage());
        }
    }
}