package com.streamnow.api.controller;

import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.dto.ContentAnalyticsDto;
import com.streamnow.api.entity.Content;
import com.streamnow.api.service.ContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin CMS", description = "Content management endpoints")
public class AdminController {

    private final ContentService contentService;

    // Create single content
    @PostMapping("/content")
    public ResponseEntity<ContentDto> createContent(@Valid @RequestBody ContentDto contentDto) {
        return ResponseEntity.ok(contentService.createContent(contentDto));
    }

    // Get all content (list)
    @GetMapping("/content")
    public ResponseEntity<List<ContentDto>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    // Get content by ID
    @GetMapping("/content/{id}")
    public ResponseEntity<ContentDto> getContentById(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }

    // Update content by ID
    @PutMapping("/content/{id}")
    public ResponseEntity<ContentDto> updateContent(
            @PathVariable String id,
            @Valid @RequestBody ContentDto contentDto) {
        return ResponseEntity.ok(contentService.updateContent(id, contentDto));
    }

    // Delete content by ID
    @DeleteMapping("/content/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable String id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    // Filter content with pagination, genre, and type
    @GetMapping("/content/filter")
    public ResponseEntity<Page<ContentDto>> filterContent(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Content.Type type,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(contentService.filterContent(genre, type, pageable));
    }

    // Bulk create content
    @PostMapping("/content/bulk")
    public ResponseEntity<List<ContentDto>> createBulkContent(@Valid @RequestBody List<ContentDto> contentList) {
        return ResponseEntity.ok(contentService.createBulkContent(contentList));
    }

    // Content analytics endpoint
    @GetMapping("/content/analytics")
    public ResponseEntity<ContentAnalyticsDto> getContentAnalytics() {
        return ResponseEntity.ok(contentService.getContentAnalytics());
    }
}
