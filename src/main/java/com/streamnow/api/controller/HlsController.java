package com.streamnow.api.controller;

import com.streamnow.api.service.HlsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hls")
@RequiredArgsConstructor
public class HlsController {

    private final HlsService hlsService;

    @GetMapping("/{contentId}/master.m3u8")
    public ResponseEntity<String> getMasterPlaylist(@PathVariable String contentId) {
        return hlsService.generateMasterPlaylist(contentId);
    }

    @GetMapping("/{contentId}/{quality}.m3u8")
    public ResponseEntity<String> getVariantPlaylist(@PathVariable String contentId, @PathVariable String quality) {
        return hlsService.generateVariantPlaylist(contentId, quality);
    }
}
