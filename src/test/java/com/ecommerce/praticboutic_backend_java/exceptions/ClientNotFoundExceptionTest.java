package com.ecommerce.praticboutic_backend_java.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Client non trouvé";
        ClientNotFoundException exception = new ClientNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Erreur lors de la recherche du client";
        Throwable cause = new RuntimeException("Base de données inaccessible");

        ClientNotFoundException exception = new ClientNotFoundException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCauseOnly() {
        Throwable cause = new IllegalArgumentException("ID invalide");

        ClientNotFoundException exception = new ClientNotFoundException(cause);

        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("ID invalide")); // le message par défaut de Exception inclut celui de la cause
    }

    @Test
    void testExceptionIsInstanceOfException() {
        ClientNotFoundException exception = new ClientNotFoundException("Test");
        assertTrue(exception instanceof Exception);
    }
}
