package com.iabdinur.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JWTUtil {

    @Value("${jwt.secret:your-256-bit-secret-key-must-be-at-least-32-characters-long}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 hours default
    private Long expiration;

    public String issueToken(String username, List<String> roles) {
        return Jwts.builder()
                .claims(Map.of("username", username, "roles", roles))
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }

    public String getSubject(String token) {
        return getTokenClaims(token).getSubject();
    }

    private Claims getTokenClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isTokenValid(String token, String username) {
        String subject = getSubject(token);
        return subject.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date today = Date.from(Instant.now());
        return getTokenClaims(token).getExpiration().before(today);
    }
}
