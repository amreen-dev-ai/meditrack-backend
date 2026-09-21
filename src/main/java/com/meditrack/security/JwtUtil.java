package com.meditrack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Pulled from application.properties -- the secret key used to SIGN tokens.
    // Anyone with this key could forge a valid token, which is why it must
    // never be hardcoded or committed in a real production app.
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // --- Step 1 of the auth flow: generate a token after successful login ---
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)                                  // WHO this token belongs to
                .issuedAt(new Date(System.currentTimeMillis()))       // WHEN it was created
                .expiration(new Date(System.currentTimeMillis() + expirationMs))  // WHEN it expires
                .signWith(getSigningKey())                            // sign it, so it can't be tampered with
                .compact();
    }

    // --- Step 4 of the auth flow: extract the username from an incoming token ---
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // --- Step 5: is this token actually valid for this user? ---
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
