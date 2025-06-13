package com.streamnow.api.config;

import com.streamnow.api.exception.AuthException;
import com.streamnow.api.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public SecurityFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Allow public endpoints without token
        if (path.startsWith("/auth/") ||
                path.startsWith("/api/subscriptions/plans") ||
                path.startsWith("/api/content/free") ||
                path.startsWith("/api/subscriptions/initiate-payment") ||
                path.startsWith("/api/subscriptions/verify-payment")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            Claims claims = jwtService.getClaims(token); // Only get claims if you need them

            // For premium content, verify subscription status from token
            if (path.startsWith("/api/content/premium") &&
                    !Boolean.TRUE.equals(claims.get("subscribed", Boolean.class))) {
                throw new AuthException("Subscription required for this content");
            }

            // Validate signature and expiration
            jwtService.validateToken(token);

            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }
}
