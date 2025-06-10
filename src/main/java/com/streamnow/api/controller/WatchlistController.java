package com.streamnow.api.controller;

import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.dto.WatchlistDto;
import com.streamnow.api.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/watchlist")
@RequiredArgsConstructor
public class WatchlistController {
    private final WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<WatchlistDto> addToWatchlist(
            @RequestParam Long userId,
            @RequestParam String contentId) {
        return ResponseEntity.ok(watchlistService.addToWatchlist(userId, contentId));
    }

    @GetMapping
    public ResponseEntity<List<ContentDto>> getWatchlist(@RequestParam Long userId) {
        return ResponseEntity.ok(watchlistService.getUserWatchlist(userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFromWatchlist(
            @RequestParam Long userId,
            @RequestParam String contentId) {
        watchlistService.removeFromWatchlist(userId, contentId);
        return ResponseEntity.noContent().build();
    }
}