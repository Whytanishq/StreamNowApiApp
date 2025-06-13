package com.streamnow.api.service;

import com.streamnow.api.config.JwtConfig;
import com.streamnow.api.entity.User;
import com.streamnow.api.repository.UserSubscriptionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    // Main token generator with all metadata
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());
        claims.put("subscribed", hasActiveSubscription(user.getId()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Simplified alternative token generator
    public String generateBasicToken(User user) {
        return Jwts.builder()
                .setClaims(new HashMap<>() {{
                    put("role", user.getRole());
                    put("email", user.getEmail());
                }})
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void updateUserClaims(User user) {
        // This would be called when subscription status changes
        // In a real implementation, you might want to invalidate existing tokens
    }

    private boolean hasActiveSubscription(Long userId) {
        return userSubscriptionRepository.existsByUserIdAndIsActiveTrue(userId);
    }
}
