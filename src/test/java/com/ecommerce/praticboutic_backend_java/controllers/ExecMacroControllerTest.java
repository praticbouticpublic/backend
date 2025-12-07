package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.services.ExecMacroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class ExecMacroControllerTest {

    private ExecMacroController controller;
    private ExecMacroService execMacroService;

    @BeforeEach
    void setUp() {
        execMacroService = mock(ExecMacroService.class, Answers.RETURNS_DEEP_STUBS);
        controller = new ExecMacroController();
        inject(controller, "macroService", execMacroService);
        inject(controller, "secretKey", "secret-123");
    }

    @Test
    @DisplayName("executeMacro - clé invalide => 403 et -1")
    void executeMacro_forbidden_whenKeyInvalid() {
        ResponseEntity<Integer> resp = controller.executeMacro("bad-key");
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        assertEquals(-1, resp.getBody());
        verifyNoInteractions(execMacroService);
    }

    @Test
    @DisplayName("executeMacro - clé valide => 200 et valeur retournée par service")
    void executeMacro_ok_whenKeyValid() {
        when(execMacroService.desactiveBoutic()).thenReturn(0);

        ResponseEntity<Integer> resp = controller.executeMacro("secret-123");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(0, resp.getBody());
        verify(execMacroService).desactiveBoutic();
        verifyNoMoreInteractions(execMacroService);
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