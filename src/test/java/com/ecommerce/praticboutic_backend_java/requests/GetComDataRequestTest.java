package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GetComDataRequestTest {

    @Test
    void testSetAndGetCmdid() {
        GetComDataRequest request = new GetComDataRequest();
        Long expectedCmdid = 1001L;
        request.setCmdid(expectedCmdid);
        assertEquals(expectedCmdid, request.getCmdid(),
                "Le getter doit retourner la valeur définie par le setter pour cmdid");
    }

    @Test
    void testSetAndGetBouticid() {
        GetComDataRequest request = new GetComDataRequest();
        Long expectedBouticid = 2002L;
        request.setBouticid(expectedBouticid);
        assertEquals(expectedBouticid, request.getBouticid(),
                "Le getter doit retourner la valeur définie par le setter pour bouticid");
    }

    @Test
    void testDefaultsAreNull() {
        GetComDataRequest request = new GetComDataRequest();
        assertAll(
                () -> assertNull(request.getCmdid(), "Le champ cmdid doit être null par défaut"),
                () -> assertNull(request.getBouticid(), "Le champ bouticid doit être null par défaut")
        );
    }

    @Test
    void testAllFieldsTogether() {
        GetComDataRequest request = new GetComDataRequest();
        Long expectedCmdid = 123L;
        Long expectedBouticid = 456L;

        request.setCmdid(expectedCmdid);
        request.setBouticid(expectedBouticid);

        assertAll(
                () -> assertEquals(expectedCmdid, request.getCmdid()),
                () -> assertEquals(expectedBouticid, request.getBouticid())
        );
    }
}
