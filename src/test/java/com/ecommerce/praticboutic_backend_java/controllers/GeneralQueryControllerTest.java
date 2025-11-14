package com.ecommerce.praticboutic_backend_java.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class GeneralQueryControllerTest {

    private GeneralQueryController controller;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        controller = new GeneralQueryController();
        entityManager = mock(EntityManager.class, Answers.RETURNS_DEEP_STUBS);
        inject(controller, "entityManager", entityManager);
    }

    @Test
    @DisplayName("processGenQuery - action listcustomer -> 200 et mapping des colonnes")
    void processGenQuery_listcustomer_ok() {
        Map<String, Object> input = new HashMap<>();
        input.put("action", "listcustomer");

        Query nquery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(nquery);

        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{1, "cust-a", "Nom A", "logoA.png", "cus_1"});
        rows.add(new Object[]{2, "cust-b", "Nom B", "logoB.png", "cus_2"});
        when(nquery.getResultList()).thenReturn(rows);

        ResponseEntity<?> resp = controller.processGenQuery(input);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        List<?> result = (List<?>) resp.getBody();
        assertEquals(2, result.size());
        List<?> first = (List<?>) result.get(0);
        assertEquals(5, first.size());
        assertEquals(1, first.get(0));
        assertEquals("cust-a", first.get(1));
        assertEquals("Nom A", first.get(2));
        assertEquals("logoA.png", first.get(3));
        assertEquals("cus_1", first.get(4));
    }

    @Test
    @DisplayName("processGenQuery - action inconnue -> 200 []")
    void processGenQuery_unknown_okEmpty() {
        Map<String, Object> input = new HashMap<>();
        input.put("action", "unknown");

        ResponseEntity<?> resp = controller.processGenQuery(input);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        List<?> result = (List<?>) resp.getBody();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("processGenQuery - exception -> 500 avec message")
    void processGenQuery_exception_500() {
        Map<String, Object> input = new HashMap<>();
        input.put("action", "listcustomer");

        when(entityManager.createNativeQuery(anyString())).thenThrow(new RuntimeException("boom"));

        ResponseEntity<?> resp = controller.processGenQuery(input);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertTrue(resp.getBody().toString().contains("boom"));
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
