package com.ecommerce.praticboutic_backend_java.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionExpiredExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Session expirée";
        SessionExpiredException exception = new SessionExpiredException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause()); // Pas de constructeur avec cause
    }

    // Comme ton exception n'accepte pas cause, on ne peut tester message+cause ou cause seul
    // On ne teste pas instanceof RuntimeException car c'est une checked exception
}
