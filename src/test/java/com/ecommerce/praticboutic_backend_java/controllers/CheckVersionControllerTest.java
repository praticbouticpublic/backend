package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.SessionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckVersionControllerTest {

    @InjectMocks
    private CheckVersionController controller;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private ObjectMapper objectMapper;

    private final String filePath = "mobileapp/authorisation.json";
    private final String jsonContent = "{\"version\":\"1.0\"}";
    private JsonNode jsonNode;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(controller, "authorizationFilePath", filePath);
        ReflectionTestUtils.setField(controller, "resourceLoader", resourceLoader);

        // JsonNode attendu pour tous les tests
        jsonNode = new ObjectMapper().readTree(jsonContent);
    }

    private void mockResource(boolean exists, InputStream inputStream) throws IOException {
        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:" + filePath)).thenReturn(resource);
        when(resource.exists()).thenReturn(exists);
        if (exists) {
            when(resource.getInputStream()).thenReturn(inputStream);
        }
    }

    @Test
    void checkVersion_fileNotFound_returns404() throws Exception {
        mockResource(false, null);

        // Utilise l'instance injectée
        ResponseEntity<?> response = controller.checkVersion(new SessionRequest());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void checkVersion_validJson_returns200() throws Exception {
        SessionRequest request = new SessionRequest();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        mockResource(true, inputStream);

        when(objectMapper.readTree(any(InputStream.class))).thenReturn(jsonNode);

        ResponseEntity<?> response = controller.checkVersion(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jsonNode, response.getBody());
    }

    @Test
    void checkVersionAlt_fileExists_returns200() throws Exception {
        SessionRequest request = new SessionRequest();
        request.setSessionId("abc");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        mockResource(true, inputStream);

        when(objectMapper.readTree(any(InputStream.class))).thenReturn(jsonNode);

        ResponseEntity<?> response = controller.checkVersionAlt(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jsonNode, response.getBody());
    }

    @Test
    void checkVersionAlt_fileNotFound_returns404() throws Exception {
        SessionRequest request = new SessionRequest();
        request.setSessionId("abc");

        mockResource(false, null);

        ResponseEntity<?> response = controller.checkVersionAlt(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void checkVersionAlt_ioException_returns500() throws Exception {
        SessionRequest request = new SessionRequest();
        request.setSessionId("abc");

        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:" + filePath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("Fichier inaccessible"));

        ResponseEntity<?> response = controller.checkVersionAlt(request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
