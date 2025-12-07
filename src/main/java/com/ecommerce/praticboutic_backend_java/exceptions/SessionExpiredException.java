// SessionExpiredException.java - Exception personnalisée
package com.ecommerce.praticboutic_backend_java.exceptions;

public class SessionExpiredException extends Exception {
    public SessionExpiredException(String message) {
        super(message);
    }
}