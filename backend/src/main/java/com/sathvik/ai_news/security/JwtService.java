package com.sathvik.ai_news.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Development secret.
    // We will move this to an environment variable before deployment.
    private static final String SECRET =
            "ai-news-development-secret-key-must-be-at-least-32-characters-long";

    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24; // 24 hours

    private final SecretKey key = Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8)
    );

    public String generateToken(Long userId, String email) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Long extractUserId(String token) {

        Claims claims = extractAllClaims(token);

        return Long.valueOf(claims.getSubject());
    }

    public String extractEmail(String token) {

        Claims claims = extractAllClaims(token);

        return claims.get("email", String.class);
    }

    public boolean isTokenValid(String token) {

        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}