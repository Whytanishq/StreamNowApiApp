package com.streamnow.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.entity.Content;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ContentDto createContent(ContentDto contentDto) {
        Content content = mapToEntity(contentDto);
        content.setId(UUID.randomUUID().toString());

        Content saved = contentRepository.save(content);
        return mapToDto(saved);
    }

    public List<ContentDto> getAllContent() {
        return contentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ContentDto getContentById(String id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        return mapToDto(content);
    }

    public ContentDto updateContent(String id, ContentDto contentDto) {
        Content existing = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        existing.setTitle(contentDto.getTitle());
        existing.setDescription(contentDto.getDescription());
        existing.setGenre(serializeGenre(contentDto.getGenre()));
        existing.setReleaseDate(contentDto.getReleaseDate());
        existing.setThumbnailUrl(contentDto.getThumbnailUrl());
        existing.setVideoUrl(contentDto.getVideoUrl());
        existing.setType(contentDto.getType());
        existing.setRating(contentDto.getRating());

        Content updated = contentRepository.save(existing);
        return mapToDto(updated);
    }

    public void deleteContent(String id) {
        if (!contentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Content not found with id: " + id);
        }
        contentRepository.deleteById(id);
    }

    // ---------- Utility Methods ----------

    private String serializeGenre(List<String> genre) {
        try {
            return objectMapper.writeValueAsString(genre);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize genre", e);
        }
    }

    private List<String> deserializeGenre(String genreJson) {
        try {
            return objectMapper.readValue(genreJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize genre", e);
        }
    }

    private ContentDto mapToDto(Content content) {
        return ContentDto.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .genre(deserializeGenre(content.getGenre()))
                .releaseDate(content.getReleaseDate())
                .thumbnailUrl(content.getThumbnailUrl())
                .videoUrl(content.getVideoUrl())
                .type(content.getType())
                .rating(content.getRating())
                .build();
    }

    private Content mapToEntity(ContentDto dto) {
        return Content.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .genre(serializeGenre(dto.getGenre()))
                .releaseDate(dto.getReleaseDate())
                .thumbnailUrl(dto.getThumbnailUrl())
                .videoUrl(dto.getVideoUrl())
                .type(dto.getType())
                .rating(dto.getRating())
                .build();
    }

    public Page<ContentDto> getAllContentPaginated(Pageable pageable) {
        return contentRepository.findAll(pageable)
                .map(ContentDto::fromEntity);
    }

    public Page<ContentDto> searchByTitle(String query, Pageable pageable) {
        return contentRepository.searchByTitle(query, pageable)
                .map(ContentDto::fromEntity);
    }

    public Page<ContentDto> filterByGenre(String genre, Pageable pageable) {
        return contentRepository.filterByGenre("\"" + genre + "\"", pageable)
                .map(ContentDto::fromEntity);
    }

    public List<ContentDto> getRecommendedContent() {
        // Simple recommendation logic - top rated content
        return contentRepository.findAll(Sort.by(Sort.Direction.DESC, "rating")).stream()
                .limit(5)
                .map(ContentDto::fromEntity)
                .collect(Collectors.toList());
    }
}
