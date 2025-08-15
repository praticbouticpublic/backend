package com.ecommerce.praticboutic_backend_java.models;

import jakarta.servlet.http.HttpSession;

import java.util.Date;
import java.util.Map;

public class JwtPayload {
    private String subject;
    private Date expiration;
    private Map<String, Object> claims;

    public JwtPayload(String subject, Date expiration, Map<String, Object> claims) {
        this.subject = subject;
        this.expiration = expiration;
        this.claims = claims;
    }

    public String getSubject() {
        return subject;
    }

    public Date getExpiration() {
        return expiration;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }


}
