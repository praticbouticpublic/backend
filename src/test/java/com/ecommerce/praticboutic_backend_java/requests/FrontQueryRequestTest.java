package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FrontQueryRequestTest {

    @Test
    void testDefaultConstructorAndSetters() {
        FrontQueryRequest request = new FrontQueryRequest();
        request.setRequete("categories");
        request.setBouticid(1);
        request.setCatid(2);
        request.setArtid(3);
        request.setGrpoptid(4);
        request.setCustomer("user123");
        request.setMethod("livraison");
        request.setTable("T1");
        request.setParam("param1");

        assertEquals("categories", request.getRequete());
        assertEquals(1, request.getBouticid());
        assertEquals(2, request.getCatid());
        assertEquals(3, request.getArtid());
        assertEquals(4, request.getGrpoptid());
        assertEquals("user123", request.getCustomer());
        assertEquals("livraison", request.getMethod());
        assertEquals("T1", request.getTable());
        assertEquals("param1", request.getParam());
    }

    @Test
    void testParameterizedConstructor() {
        FrontQueryRequest request = new FrontQueryRequest("articles");
        assertEquals("articles", request.getRequete());
        assertNull(request.getBouticid());
        assertNull(request.getCatid());
    }

    @Test
    void testToStringContainsAllFields() {
        FrontQueryRequest request = FrontQueryRequest.builder()
                .requete("categories")
                .bouticid(1)
                .catid(2)
                .artid(3)
                .grpoptid(4)
                .customer("user123")
                .method("sur place")
                .table("T2")
                .param("paramX")
                .build();

        String str = request.toString();
        assertTrue(str.contains("requete='categories'"));
        assertTrue(str.contains("bouticid=1"));
        assertTrue(str.contains("catid=2"));
        assertTrue(str.contains("artid=3"));
        assertTrue(str.contains("grpoptid=4"));
        assertTrue(str.contains("customer='user123'"));
        assertTrue(str.contains("method='sur place'"));
        assertTrue(str.contains("table='T2'"));
        assertTrue(str.contains("param='paramX'"));
    }

    @Test
    void testBuilderSetsAllFields() {
        FrontQueryRequest request = FrontQueryRequest.builder()
                .requete("options")
                .bouticid(10)
                .catid(20)
                .artid(30)
                .grpoptid(40)
                .customer("customerABC")
                .method("livraison")
                .table("T5")
                .param("paramY")
                .build();

        assertAll(
                () -> assertEquals("options", request.getRequete()),
                () -> assertEquals(10, request.getBouticid()),
                () -> assertEquals(20, request.getCatid()),
                () -> assertEquals(30, request.getArtid()),
                () -> assertEquals(40, request.getGrpoptid()),
                () -> assertEquals("customerABC", request.getCustomer()),
                () -> assertEquals("livraison", request.getMethod()),
                () -> assertEquals("T5", request.getTable()),
                () -> assertEquals("paramY", request.getParam())
        );
    }

    @Test
    void testDefaultsAreNull() {
        FrontQueryRequest request = new FrontQueryRequest();
        assertAll(
                () -> assertNull(request.getRequete()),
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
