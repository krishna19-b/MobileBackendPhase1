package com.krishna.MobileBackendProjectPhase1.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTutil {

    private final SecretKey key;

    private final long expirationTime;

    public JWTutil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expirationTime) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        this.expirationTime = expirationTime;
    }

    public String token(UserDetails userDetails) {

        return Jwts.builder()
                .setSubject(userDetails.getUsername())

                .claim("role", userDetails.getAuthorities()
                                .stream()
                                .findFirst()
                                .map(authority ->
                                        authority.getAuthority())
                                .orElse("ROLE_USER")
                )

                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String findUsername(String token) {

        return jwtClaims(token).getSubject();
    }

    public Claims jwtClaims(String token) {

        return Jwts.parser().verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {

        return jwtClaims(token).getExpiration().before(new Date());
    }

    public boolean validate(UserDetails userDetails, String username, String token) {

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
}