package com.streamnow.api.service;

import com.streamnow.api.config.JavaConfig;
import com.streamnow.api.entity.Content;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HlsService {
    private final ContentRepository contentRepository;
    private final JavaConfig config;

    public ResponseEntity<String> generateMasterPlaylist(String contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        String baseUrl = config.getCdnBaseUrl();

        String manifest = "#EXTM3U\n" +
                "#EXT-X-VERSION:3\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=800000,RESOLUTION=640x360\n" +
                baseUrl + "/hls/" + contentId + "/360p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=1400000,RESOLUTION=854x480\n" +
                baseUrl + "/hls/" + contentId + "/480p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=1280x720\n" +
                baseUrl + "/hls/" + contentId + "/720p.m3u8\n" +
                "#EXT-X-STREAM-INF:BANDWIDTH=5000000,RESOLUTION=1920x1080\n" +
                baseUrl + "/hls/" + contentId + "/1080p.m3u8";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .body(manifest);
    }

    public ResponseEntity<String> generateVariantPlaylist(String contentId, String quality) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        int targetDuration = 6; // Segment duration in seconds
        int segmentCount = (int) Math.ceil(content.getDurationMinutes() * 60.0 / targetDuration);

        StringBuilder playlist = new StringBuilder();
        playlist.append("#EXTM3U\n")
                .append("#EXT-X-VERSION:3\n")
                .append("#EXT-X-TARGETDURATION:").append(targetDuration).append("\n")
                .append("#EXT-X-MEDIA-SEQUENCE:0\n");

        for (int i = 0; i < segmentCount; i++) {
            playlist.append("#EXTINF:").append(targetDuration).append(",\n")
                    .append(config.getCdnBaseUrl()).append("/segments/")
                    .append(contentId).append("/").append(quality)
                    .append("/").append(i).append(".ts\n");
        }

        playlist.append("#EXT-X-ENDLIST");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .body(playlist.toString());
    }
}