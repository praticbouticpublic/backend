package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FrontRequestTest {

    @Test
    void testSetAndGetRequete() {
        FrontRequest request = new FrontRequest();
        String expected = "categories";
        request.setRequete(expected);
        assertEquals(expected, request.getRequete());
    }

    @Test
    void testSetAndGetSessionId() {
        FrontRequest request = new FrontRequest();
        String expected = "session_123";
        request.setSessionId(expected);
        assertEquals(expected, request.getSessionId());
    }

    @Test
    void testSetAndGetBouticid() {
        FrontRequest request = new FrontRequest();
        Integer expected = 1;
        request.setBouticid(expected);
        assertEquals(expected, request.getBouticid());
    }

    @Test
    void testSetAndGetCatid() {
        FrontRequest request = new FrontRequest();
        Integer expected = 2;
        request.setCatid(expected);
        assertEquals(expected, request.getCatid());
    }

    @Test
    void testSetAndGetArtid() {
        FrontRequest request = new FrontRequest();
        Integer expected = 3;
        request.setArtid(expected);
        assertEquals(expected, request.getArtid());
    }

    @Test
    void testSetAndGetGrpoptid() {
        FrontRequest request = new FrontRequest();
        Integer expected = 4;
        request.setGrpoptid(expected);
        assertEquals(expected, request.getGrpoptid());
    }

    @Test
    void testSetAndGetCustomer() {
        FrontRequest request = new FrontRequest();
        String expected = "user123";
        request.setCustomer(expected);
        assertEquals(expected, request.getCustomer());
    }

    @Test
    void testSetAndGetMethod() {
        FrontRequest request = new FrontRequest();
        String expected = "livraison";
        request.setMethod(expected);
        assertEquals(expected, request.getMethod());
    }

    @Test
    void testSetAndGetTable() {
        FrontRequest request = new FrontRequest();
        String expected = "T1";
        request.setTable(expected);
        assertEquals(expected, request.getTable());
    }

    @Test
    void testSetAndGetParam() {
        FrontRequest request = new FrontRequest();
        String expected = "paramX";
        request.setParam(expected);
        assertEquals(expected, request.getParam());
    }

    @Test
    void testDefaultsAreNull() {
        FrontRequest request = new FrontRequest();
        assertAll(
                () -> assertNull(request.getRequete()),
                () -> assertNull(request.getSessionId()),
                () -> assertNull(request.getBouticid()),
                () -> assertNull(request.getCatid()),
                () -> assertNull(request.getArtid()),
                () -> assertNull(request.getGrpoptid()),
                () -> assertNull(request.getCustomer()),
                () -> assertNull(request.getMethod()),
                () -> assertNull(request.getTable()),
                () -> assertNull(request.getParam())
        );
    }
}
