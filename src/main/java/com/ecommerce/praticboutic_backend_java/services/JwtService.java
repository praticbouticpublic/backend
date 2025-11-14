package com.ecommerce.praticboutic_backend_java.services;


import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    // Clé secrète pour signer les tokens (à externaliser en prod !)
    private static final byte[] SECRET_BYTES = "04696efc60c60d1723388cc0f2eb239015c6acc654d6b2b3324a8779c3ca8b32".getBytes();
    private static final Key key = Keys.hmacShaKeyFor(SECRET_BYTES);
            //.secretKeyFor(SignatureAlgorithm.HS256);

    // Durée de validité : 1 mois
    private static final long jwtExpirationMs = 30L * 24 * 60 * 60 * 1000; // 30 jours en ms
    private Map<String, Object> claims;

/*
    // Lire l'identité dans un token
    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Vérifier si le token est valide
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Facultatif : expose la date d’expiration
    public Date getExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
*/

    /**
     * Génère un token JWT à partir d'un map de claims
     * (optionnel, si tu souhaites générer des tokens dans ce service)
     */
    public static String generateToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public static JwtPayload parseToken(String token) {
        Jws<Claims> jwsClaims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token);


        Claims claims = jwsClaims.getPayload();


        String subject = claims.getSubject();
        Date expiration = claims.getExpiration();

        // On copie les claims dans une nouvelle map, en retirant subject et expiration pour éviter doublons
        Map<String, Object> claimsMap = new HashMap<>(claims);
        claimsMap.remove(Claims.SUBJECT);
        claimsMap.remove(Claims.EXPIRATION);

        // Créer et retourner un objet JwtPayload
        return new JwtPayload(subject, expiration, claimsMap);
    }

    public boolean isAuthenticated(Map<String, Object> payload) {

        return ((payload.get("bo_auth") != null) && (payload.get("bo_auth").equals("oui")));
    }

    public void updateToken(Map<String, Object> sessionData, String token) {
        Jws<Claims> jwsClaims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token);

        Claims claims = jwsClaims.getPayload();
        String subject = claims.getSubject();

        claims.putAll(sessionData);
        generateToken(sessionData, subject);

    }
    
    public void setClaims(Map<String, Object> claims) {
        this.claims = claims;
    }





}
