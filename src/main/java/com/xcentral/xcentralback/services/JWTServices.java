package com.xcentral.xcentralback.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.security.Key;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xcentral.xcentralback.models.User;

@Component
public class JWTServices {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration:36000}")
    private int jwtExpirationInSeconds;

    private static final Logger logger = LoggerFactory.getLogger(JWTServices.class);

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        return createToken(claims, user.getUsername());
    }

    private String createToken(Map<String, Object> claims, String username) {
        logger.info("Creating token for user: {}", username);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000L))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        try {
            if (SECRET == null || SECRET.trim().isEmpty()) {
                throw new IllegalStateException("JWT secret is not configured. Please set the JWT_SECRET environment variable.");
            }
            
            byte[] keyBytes = Decoders.BASE64.decode(SECRET);
            if (keyBytes.length < 32) {
                throw new IllegalStateException("JWT secret key is too short. Must be at least 32 bytes (256 bits) when BASE64 decoded.");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("JWT secret is not valid BASE64. Please provide a valid BASE64 encoded secret that is at least 32 bytes when decoded.", e);
        }
    }
}
