package com.urlshortener.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generates a JWT token for a username.
     * 
     * @param username The username to generate token for
     * @return JWT token string
     * 
     * @requires username != null && !username.isEmpty()
     * @ensures \result != null && !\result.isEmpty()
     */
    public String generateToken(String username) {
        //@ assert username != null && !username.isEmpty();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        //@ assert \result != null && !\result.isEmpty();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Gets username from JWT token.
     * 
     * @param token The JWT token
     * @return Username extracted from token
     * 
     * @requires token != null && !token.isEmpty() && validateToken(token)
     * @ensures \result != null && !\result.isEmpty()
     */
    public String getUsernameFromToken(String token) {
        //@ assert token != null && !token.isEmpty();
        
        Claims claims = getClaimsFromToken(token);
        String username = claims.getSubject();
        
        //@ assert username != null && !username.isEmpty();
        return username;
    }

    /**
     * Validates a JWT token.
     * 
     * @param token The JWT token to validate
     * @return true if token is valid, false otherwise
     * 
     * @requires token != null && !token.isEmpty()
     * @ensures \result == (token is valid and not expired)
     */
    public boolean validateToken(String token) {
        //@ assert token != null && !token.isEmpty();
        
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

