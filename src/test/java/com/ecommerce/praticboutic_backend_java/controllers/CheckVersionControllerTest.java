package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.SessionRequest;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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
    private CheckVersionController checkVersionController;

    @Mock
    private SessionService sessionService;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Simuler le chemin du fichier depuis application.properties
        ReflectionTestUtils.setField(checkVersionController, "authorizationFilePath", "mobileapp/authorisation.json");


    }

    @Test
    public void testCheckVersion_fileNotFound_returnsNotFound() {
        ReflectionTestUtils.setField(checkVersionController, "authorizationFilePath", "mobileapp/authorisation.json");

        SessionRequest request = new SessionRequest();

        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:mobileapp/authorisation.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        ResponseEntity<?> response = checkVersionController.checkVersion(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testCheckVersion_validJson_returnsOk() throws Exception {
        ReflectionTestUtils.setField(checkVersionController, "authorizationFilePath", "mobileapp/authorisation.json");

        SessionRequest request = new SessionRequest();
        String jsonContent = "{\"version\":\"1.0\"}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());

        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:mobileapp/authorisation.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(inputStream);

        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);
        when(objectMapper.readTree(any(InputStream.class))).thenReturn(jsonNode);

        ResponseEntity<?> response = checkVersionController.checkVersion(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(jsonNode, response.getBody());
    }


    @Test
    public void testCheckVersionAlt_fileExists_returnsOk() throws Exception {
        SessionRequest request = new SessionRequest();
        request.setSessionId("abc");

        String jsonContent = "{\"version\":\"1.0\"}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        Resource resource = mock(Resource.class);

        when(resourceLoader.getResource("classpath:mobileapp/authorisation.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(inputStream);

        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);
        when(objectMapper.readTree(any(InputStream.class))).thenReturn(jsonNode);

        ResponseEntity<?> response = checkVersionController.checkVersionAlt(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(jsonNode, response.getBody());
    }

    @Test
    public void testCheckVersionAlt_fileNotFound_returnsNotFound() {
        SessionRequest request = new SessionRequest();
        request.setSessionId("abc");

        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:mobileapp/authorisation.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        ResponseEntity<?> response = checkVersionController.checkVersionAlt(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testCheckVersionAlt_ioException_returnsServerError() throws Exception {
        SessionRequest request = new SessionRequest();
        request.setSessionId("abc");

        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:mobileapp/authorisation.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("Fichier inaccessible"));

        ResponseEntity<?> response = checkVersionController.checkVersionAlt(request);
        assertEquals(500, response.getStatusCodeValue());
    }
}

