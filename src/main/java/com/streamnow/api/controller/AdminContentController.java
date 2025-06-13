package com.streamnow.api.controller;

import com.streamnow.api.config.VideoTranscoder;
import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.entity.Content;
import com.streamnow.api.repository.ContentRepository;
import com.streamnow.api.service.ContentService;
import com.streamnow.api.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/content")
@RequiredArgsConstructor
public class AdminContentController {

    private final ContentService contentService;
    private final VideoTranscoder transcoder;
    private final ContentRepository contentRepository;
    private final JwtService jwtService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Content> uploadContent(
            @RequestPart ContentDto contentDto,
            @RequestPart MultipartFile videoFile,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {

        // === Extract token and validate ===
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(null);
        }
        String token = authHeader.substring(7);
        Claims claims = jwtService.getClaims(token);
        jwtService.validateToken(token);

        // === Check if user is admin ===
        String role = claims.get("role", String.class);
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(null);
        }

        // === Proceed with saving video ===
        String originalPath = saveVideoFile(videoFile);
        Map<String, String> transcodedPaths = transcoder.transcodeVideo(originalPath);
        Map<String, String> hlsManifests = transcoder.generateHlsManifests(transcodedPaths);

        Content content = new Content();
        content.setId(UUID.randomUUID().toString());
        content.setTitle(contentDto.getTitle());
        content.setDescription(contentDto.getDescription());
        content.setDuration(contentDto.getDurationMinutes());
        content.setReleaseDate(contentDto.getReleaseDate());
        content.setCategories(contentDto.getCategories());
        content.setThumbnailUrl(generateThumbnail(originalPath));
        content.setVideoUrl(originalPath);
        content.setGenre(toJson(contentDto.getGenre()));
        content.setType(contentDto.getType());
        content.setRating(contentDto.getRating());
        content.setHlsManifests(hlsManifests);

        Content savedContent = contentRepository.save(content);
        return ResponseEntity.ok(savedContent);
    }


    private String saveVideoFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("media/videos/original", filename);
        Files.createDirectories(path.getParent());
        file.transferTo(path);
        return path.toString();
    }

    private String generateThumbnail(String videoPath) throws IOException {
        String thumbnailPath = videoPath.replace(".mp4", ".jpg");
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", videoPath,
                "-ss", "00:00:05",
                "-vframes", "1",
                thumbnailPath);
        try {
            pb.start().waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thumbnail generation was interrupted", e);
        }
        return thumbnailPath;
    }

    private String toJson(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
