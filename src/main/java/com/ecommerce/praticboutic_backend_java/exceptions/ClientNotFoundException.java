package com.ecommerce.praticboutic_backend_java.exceptions;

/**
 * Exception levée lorsqu'un client n'est pas trouvé dans la base de données.
 * Cette exception est généralement utilisée lors de la recherche d'un client
 * par email ou par identifiant.
 */
public class ClientNotFoundException extends Exception {
    
    /**
     * Constructeur avec un message d'erreur.
     * 
     * @param message le message décrivant l'erreur
     */
    public ClientNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec message et cause.
     * 
     * @param message le message décrivant l'erreur
     * @param cause la cause de l'exception
     */
    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructeur avec la cause uniquement.
     * 
     * @param cause la cause de l'exception
     */
    public ClientNotFoundException(Throwable cause) {
        super(cause);
    }
}