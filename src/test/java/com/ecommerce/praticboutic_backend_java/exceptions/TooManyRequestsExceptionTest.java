package com.ecommerce.praticboutic_backend_java.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TooManyRequestsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Trop de requêtes envoyées";
        TooManyRequestsException exception = new TooManyRequestsException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Trop de requêtes en peu de temps";
        Throwable cause = new RuntimeException("Rate limit exceeded");

        TooManyRequestsException exception = new TooManyRequestsException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCauseOnly() {
        Throwable cause = new IllegalStateException("Abus détecté");
        TooManyRequestsException exception = new TooManyRequestsException(cause);

        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Abus détecté"));
    }

    @Test
    void testGetRetryAfterSecondsReturnsMinusOneByDefault() {
        TooManyRequestsException exception = new TooManyRequestsException("Test");
        assertEquals(-1, exception.getRetryAfterSeconds());
    }

    @Test
    void testInstanceOfException() {
        TooManyRequestsException exception = new TooManyRequestsException("Test");
        assertTrue(exception instanceof Exception);
    }
}
