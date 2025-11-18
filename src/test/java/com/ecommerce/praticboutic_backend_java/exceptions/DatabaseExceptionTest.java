package com.ecommerce.praticboutic_backend_java.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseExceptionTest {

    @Test
    void testInvalidSessionDataExceptionMessage() {
        String message = "Session invalide";
        DatabaseException.InvalidSessionDataException exception =
                new DatabaseException.InvalidSessionDataException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testEmailAlreadyExistsExceptionMessage() {
        String message = "L'adresse email existe déjà";
        DatabaseException.EmailAlreadyExistsException exception =
                new DatabaseException.EmailAlreadyExistsException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testInvalidAliasExceptionMessage() {
        String message = "Alias invalide";
        DatabaseException.InvalidAliasException exception =
                new DatabaseException.InvalidAliasException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testDatabaseExceptionIsChecked() {
        Exception exception = new DatabaseException();
        assertTrue(exception instanceof Exception);
        assertFalse(exception instanceof RuntimeException);
    }
}
