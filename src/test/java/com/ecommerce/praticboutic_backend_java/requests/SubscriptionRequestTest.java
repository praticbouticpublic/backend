package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionRequestTest {

    @Test
    void testSetAndGetPriceid() {
        SubscriptionRequest request = new SubscriptionRequest();
        String expectedPriceid = "price_12345";
        request.setPriceid(expectedPriceid);
        assertEquals(expectedPriceid, request.getPriceid(),
                "Le getter doit retourner la valeur définie par le setter pour priceid");
    }

    @Test
    void testDefaultIsNull() {
        SubscriptionRequest request = new SubscriptionRequest();
        assertNull(request.getPriceid(), "Le champ priceid doit être null par défaut");
    }
}
