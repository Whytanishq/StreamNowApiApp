package com.streamnow.api.controller;

import com.streamnow.api.service.JwtService;
import com.streamnow.api.service.RatingService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;
    private final JwtService jwtService;


    @PostMapping
    public ResponseEntity<Void> rateContent(
            @RequestHeader("Authorization") String token,
            @RequestParam String contentId,
            @RequestParam int rating) {
        // Extract user ID from JWT
        Long userId = getUserIdFromToken(token);
        ratingService.rateContent(userId, contentId, rating);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Integer> getUserRating(
            @RequestHeader("Authorization") String token,
            @RequestParam String contentId) {
        Long userId = getUserIdFromToken(token);
        Double rating = ratingService.getUserRating(userId, contentId);
        return ResponseEntity.ok(rating != null ? rating.intValue() : 0);
    }

    private Long getUserIdFromToken(String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Get claims using JwtService
        Claims claims = jwtService.getClaims(token);

        // Extract user ID from subject (sub) claim
        return Long.parseLong(claims.getSubject());
    }

}