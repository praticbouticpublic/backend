package com.ecommerce.praticboutic_backend_java.exceptions;

public class DatabaseException extends Exception{
    /**
     * Classes d'exceptions personnalisées pour mieux gérer les erreurs
     */
    public static class InvalidSessionDataException extends RuntimeException {
        public InvalidSessionDataException(String message) {
            super(message);
        }
    }

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InvalidAliasException extends RuntimeException {
        public InvalidAliasException(String message) {
            super(message);
        }
    }
}
