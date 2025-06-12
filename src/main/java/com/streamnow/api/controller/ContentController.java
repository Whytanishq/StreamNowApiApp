package com.streamnow.api.controller;

import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.service.ContentService;
import com.streamnow.api.service.JwtService;
import com.streamnow.api.service.ViewHistoryService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor

public class ContentController {
    private final ContentService contentService;
    private final JwtService jwtService;
    private final ViewHistoryService viewHistoryService;

    @GetMapping
    public ResponseEntity<List<ContentDto>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ContentDto>> getAllContentPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contentService.getAllContentPaginated(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ContentDto>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contentService.searchByTitle(title, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ContentDto>> filterByGenre(
            @RequestParam String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contentService.filterByGenre(genre, pageable));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<ContentDto>> getRecommendedContent() {
        return ResponseEntity.ok(contentService.getRecommendedContent());
    }

    // Add to ContentController.java
    @GetMapping("/trending")
    public ResponseEntity<List<ContentDto>> getTrendingContent() {
        return ResponseEntity.ok(contentService.getTrendingContent());
    }


    @GetMapping(value = "/personalized")
    public ResponseEntity<List<ContentDto>> getPersonalizedContent(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(contentService.getPersonalizedContent(token));
    }

    @GetMapping("/continue-watching")
    public ResponseEntity<List<ContentDto>> getContinueWatching(
            @RequestHeader("Authorization") String token) {
        Claims claims = jwtService.getClaims(token.replace("Bearer ", ""));
        Long userId = Long.parseLong(claims.getSubject());
        return ResponseEntity.ok(viewHistoryService.getContinueWatching(userId));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ContentDto> getContentById(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }
}