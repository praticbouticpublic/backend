package com.ecommerce.praticboutic_backend_java.responses;

import com.ecommerce.praticboutic_backend_java.responses.ApiResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testConstructorSetsFieldsCorrectly() {
        // Arrange
        boolean success = true;
        String message = "Operation successful";

        // Act
        ApiResponse response = new ApiResponse(success, message);

        // Assert
        assertEquals(success, getField(response, "success"));
        assertEquals(message, getField(response, "message"));
        assertNull(getField(response, "data"));
    }

    // Méthode utilitaire pour accéder aux champs privés
    private Object getField(Object obj, String fieldName) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            fail("Erreur d'accès au champ : " + fieldName + " -> " + e.getMessage());
            return null;
        }
    }
}

