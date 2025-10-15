package com.ecommerce.praticboutic_backend_java.responses;


import com.ecommerce.praticboutic_backend_java.responses.LoginResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void testSetAndGetCustomerId() {
        // Arrange
        LoginResponse response = new LoginResponse();
        Integer expectedId = 42;

        // Act
        response.setCustomerId(expectedId);

        // Assert
        assertEquals(expectedId, response.getCustomerId(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testSetAndGetCustomer() {
        // Arrange
        LoginResponse response = new LoginResponse();
        String expectedCustomer = "John Doe";

        // Act
        response.setCustomer(expectedCustomer);

        // Assert
        assertEquals(expectedCustomer, response.getCustomer(),
                "Le getter doit retourner la valeur définie par le setter pour customer");
    }

    @Test
    void testSetAndGetStripeCustomerId() {
        // Arrange
        LoginResponse response = new LoginResponse();
        String expectedStripeId = "cus_ABC123";

        // Act
        response.setStripeCustomerId(expectedStripeId);

        // Assert
        assertEquals(expectedStripeId, response.getStripeCustomerId(),
                "Le getter doit retourner la valeur définie par le setter pour stripecustomerid");
    }

    @Test
    void testSetAndGetSubscriptionStatus() {
        // Arrange
        LoginResponse response = new LoginResponse();
        String expectedStatus = "active";

        // Act
        response.setSubscriptionStatus(expectedStatus);

        // Assert
        assertEquals(expectedStatus, response.getSubscriptionStatus(),
                "Le getter doit retourner la valeur définie par le setter pour subscriptionstatus");
    }

    @Test
    void testAllFieldsTogether() {
        // Arrange
        LoginResponse response = new LoginResponse();

        Integer expectedId = 99;
        String expectedCustomer = "Alice";
        String expectedStripeId = "cus_XYZ789";
        String expectedStatus = "inactive";

        // Act
        response.setCustomerId(expectedId);
        response.setCustomer(expectedCustomer);
        response.setStripeCustomerId(expectedStripeId);
        response.setSubscriptionStatus(expectedStatus);

        // Assert
        assertAll(
                () -> assertEquals(expectedId, response.getCustomerId()),
                () -> assertEquals(expectedCustomer, response.getCustomer()),
                () -> assertEquals(expectedStripeId, response.getStripeCustomerId()),
                () -> assertEquals(expectedStatus, response.getSubscriptionStatus())
        );
    }
}
