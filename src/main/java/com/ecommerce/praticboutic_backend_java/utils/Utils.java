package com.ecommerce.praticboutic_backend_java.utils;

import org.springframework.web.util.HtmlUtils;

import java.util.Base64;

public class Utils {
    /**
     * Méthode utilitaire pour valider les noms de propriété afin d'éviter l'injection SQL
     */
    public static void validatePropertyName(String propertyName) {
        // Vérifier que le nom de propriété ne contient que des caractères alphanumériques et des underscores
        if (!propertyName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Nom de propriété invalide: " + propertyName);
        }
    }

    /**
     * Sanitize les entrées utilisateur pour prévenir les injections
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Implémentation de base - à adapter selon les besoins de sécurité
        return HtmlUtils.htmlEscape(input.trim());
    }

    public static String encryptCode(String code, String key, byte[] iv) throws Exception {
        // Implement AES-256-CBC encryption here
        // For example: use Cipher class in Java with "AES/CBC/PKCS5Padding"
        // Placeholder for encryption logic
        return Base64.getEncoder().encodeToString(code.getBytes());
    }
}
