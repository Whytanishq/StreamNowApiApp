package com.streamnow.api.controller;

import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.service.ContentService;
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

    @GetMapping
    public ResponseEntity<List<ContentDto>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentDto> getContentById(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getContentById(id));
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
}