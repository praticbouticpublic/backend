package com.ecommerce.praticboutic_backend_java.exceptions;

/**
 * Exception levée lorsque le nombre de requêtes dépasse la limite autorisée
 * dans un intervalle de temps donné. Cette exception est généralement utilisée
 * pour implémenter une protection contre les attaques par force brute ou
 * pour limiter le taux de requêtes (rate limiting).
 */
public class TooManyRequestsException extends Exception {
    
    /**
     * Constructeur avec un message d'erreur.
     * 
     * @param message le message décrivant l'erreur
     */
    public TooManyRequestsException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec message et cause.
     * 
     * @param message le message décrivant l'erreur
     * @param cause la cause de l'exception
     */
    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructeur avec la cause uniquement.
     * 
     * @param cause la cause de l'exception
     */
    public TooManyRequestsException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Permet de récupérer le temps d'attente recommandé (si défini).
     * Cette méthode peut être implémentée pour retourner un délai d'attente
     * avant que l'utilisateur puisse réessayer.
     * 
     * @return le temps d'attente en secondes ou -1 si non défini
     */
    public int getRetryAfterSeconds() {
        return -1;
    }
}