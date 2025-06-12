package com.streamnow.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamnow.api.dto.ContentAnalyticsDto;
import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.entity.Content;
import com.streamnow.api.entity.ContentRating;
import com.streamnow.api.entity.User;
import com.streamnow.api.entity.ViewHistory;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.*;
import com.streamnow.api.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ContentRatingRepository ratingRepository;
    private final ViewHistoryRepository viewHistoryRepository;



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

    @Cacheable("content")
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
        return contentRepository.filterByGenre(
                List.of(genre), // Java 9+ (or use Collections.singletonList(genre))
                pageable
        ).map(ContentDto::fromEntity);
    }


    public List<ContentDto> getRecommendedContent() {
        return contentRepository.findAll(Sort.by(Sort.Direction.DESC, "rating")).stream()
                .limit(5)
                .map(ContentDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<ContentDto> filterContent(String genre, Content.Type type, Pageable pageable) {
        Page<Content> filteredContent;

        if (genre != null && type != null) {
            filteredContent = contentRepository.findByGenreContainingAndType(genre, type, pageable);
        } else if (genre != null) {
            filteredContent = contentRepository.findByGenreContaining(genre, pageable);
        } else if (type != null) {
            filteredContent = contentRepository.findByType(type, pageable);
        } else {
            filteredContent = contentRepository.findAll(pageable);
        }

        return filteredContent.map(this::mapToDto);
    }

    public List<ContentDto> getTrendingContent() {
        Pageable pageable = PageRequest.of(0, 5);
        return contentRepository.findTrending(pageable)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ====== NEW METHODS BELOW ======

    public List<ContentDto> createBulkContent(List<ContentDto> contentDtos) {
        return contentDtos.stream()
                .map(this::createContent)
                .collect(Collectors.toList());
    }

    public ContentAnalyticsDto getContentAnalytics() {
        long totalContent = contentRepository.count();
        long moviesCount = contentRepository.countByType(Content.Type.MOVIE);
        long showsCount = contentRepository.countByType(Content.Type.TV_SHOW);
        double averageRating = contentRepository.getAverageRating();
        Map<String, Long> genreDistribution = contentRepository.getGenreDistribution()
                .stream()
                .collect(Collectors.toMap(
                        ContentRepository.GenreCount::getGenre,
                        ContentRepository.GenreCount::getCount
                ));

        return ContentAnalyticsDto.builder()
                .totalContent(totalContent)
                .moviesCount(moviesCount)
                .showsCount(showsCount)
                .genreDistribution(genreDistribution)
                .averageRating(averageRating)
                .build();
    }

    public List<ContentDto> getPersonalizedContent(String token) {
        try {
            // Properly handle token format (remove "Bearer " prefix if present)
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Claims claims = jwtService.getClaims(cleanToken);
            Long userId = Long.parseLong(claims.getSubject());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            return getRecommendedContent(user);
        } catch (Exception e) {
            // Fallback to popular content if any error occurs
            return getPopularContent();
        }
    }

    public List<ContentDto> getRecommendedContent(User user) {
        // 1. Get user's watch history and ratings
        List<ViewHistory> watchHistory = viewHistoryRepository.findByUserId(user.getId());
        List<ContentRating> userRatings = ratingRepository.findByUserId(user.getId());

        // 2. Find similar users (users who rated the same content similarly)
        List<Long> similarUserIds = findSimilarUsers(user, userRatings);

        // 3. Get top-rated content from similar users
        List<ContentDto> similarUsersContent = getContentFromSimilarUsers(similarUserIds);

        // 4. Fallback to popular content
        if (similarUsersContent.isEmpty()) {
            return getPopularContent();
        }

        return similarUsersContent;
    }

    private List<Long> findSimilarUsers(User user, List<ContentRating> userRatings) {
        // Simple implementation - find users who rated the same content similarly
        return ratingRepository.findSimilarUsers(
                user.getId(),
                userRatings.stream()
                        .map(r -> r.getContent().getId())
                        .collect(Collectors.toList()));
    }

    private List<ContentDto> getContentFromSimilarUsers(List<Long> similarUserIds) {
        return ratingRepository.findTopRatedContentByUsers(similarUserIds, PageRequest.of(0, 10))
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private List<ContentDto> getPopularContent() {
        return contentRepository.findAll(Sort.by(Sort.Direction.DESC, "rating"))
                .stream()
                .limit(10)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
