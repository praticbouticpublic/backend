package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuildBouticRequestTest {

    @Test
    void testSetAndGetDeviceId() {
        // Arrange
        BuildBouticRequest request = new BuildBouticRequest();
        String expectedDeviceId = "device_123";

        // Act
        request.setDeviceId(expectedDeviceId);

        // Assert
        assertEquals(expectedDeviceId, request.getDeviceId(),
                "Le getter doit retourner la valeur définie par le setter pour deviceId");
    }

    @Test
    void testSetAndGetDeviceType() {
        // Arrange
        BuildBouticRequest request = new BuildBouticRequest();
        Integer expectedDeviceType = 1;

        // Act
        request.setDeviceType(expectedDeviceType);

        // Assert
        assertEquals(expectedDeviceType, request.getDeviceType(),
                "Le getter doit retourner la valeur définie par le setter pour deviceType");
    }

    @Test
    void testToStringContainsFields() {
        // Arrange
        BuildBouticRequest request = new BuildBouticRequest();
        request.setDeviceId("device_abc");
        request.setDeviceType(2);

        // Act
        String str = request.toString();

        // Assert
        assertTrue(str.contains("deviceId='device_abc'"), "toString doit contenir deviceId");
        assertTrue(str.contains("deviceType=2"), "toString doit contenir deviceType");
    }

    @Test
    void testAllFieldsTogether() {
        // Arrange
        BuildBouticRequest request = new BuildBouticRequest();
        String expectedDeviceId = "device_xyz";
        Integer expectedDeviceType = 3;

        // Act
        request.setDeviceId(expectedDeviceId);
        request.setDeviceType(expectedDeviceType);

        // Assert
        assertAll(
                () -> assertEquals(expectedDeviceId, request.getDeviceId()),
                () -> assertEquals(expectedDeviceType, request.getDeviceType())
        );
    }
}
