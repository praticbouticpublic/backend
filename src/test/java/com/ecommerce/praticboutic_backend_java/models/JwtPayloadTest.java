package com.ecommerce.praticboutic_backend_java.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtPayloadTest {

    private JwtPayload jwtPayload;
    private Date expiration;
    private Map<String, Object> claims;

    @BeforeEach
    void setUp() {
        expiration = new Date();
        claims = new HashMap<>();
        claims.put("role", "USER");
        claims.put("customId", 123);

        jwtPayload = new JwtPayload("user@example.com", expiration, claims);
    }

    @Test
    void testGetSubject() {
        assertEquals("user@example.com", jwtPayload.getSubject());
    }

    @Test
    void testGetExpiration() {
        assertEquals(expiration, jwtPayload.getExpiration());
    }

    @Test
    void testGetClaims() {
        assertEquals(claims, jwtPayload.getClaims());
        assertEquals("USER", jwtPayload.getClaims().get("role"));
        assertEquals(123, jwtPayload.getClaims().get("customId"));
    }

    @Test
    void testEmptyClaimsMap() {
        JwtPayload payload = new JwtPayload("admin@example.com", null, new HashMap<>());
        assertEquals("admin@example.com", payload.getSubject());
        assertNull(payload.getExpiration());
        assertTrue(payload.getClaims().isEmpty());
    }
}
