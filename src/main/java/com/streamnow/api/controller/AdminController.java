package com.streamnow.api.controller;

import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.entity.Content;
import com.streamnow.api.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/content")
@RequiredArgsConstructor
public class AdminController {
    private final ContentService contentService;

    @PostMapping
    public ResponseEntity<ContentDto> createContent(@Valid @RequestBody ContentDto contentDto) {
        return ResponseEntity.ok(contentService.createContent(contentDto));
    }

    @GetMapping
    public ResponseEntity<List<ContentDto>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentDto> getContentById(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentDto> updateContent(
            @PathVariable String id,
            @Valid @RequestBody ContentDto contentDto) {
        return ResponseEntity.ok(contentService.updateContent(id, contentDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable String id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    // Add to AdminController.java
    @GetMapping("/filter")
    public ResponseEntity<Page<ContentDto>> filterContent(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Content.Type type,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(contentService.filterContent(genre, type, pageable));
    }
}