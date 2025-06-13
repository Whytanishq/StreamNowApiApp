package com.streamnow.api.service;

import com.streamnow.api.entity.Content;
import com.streamnow.api.entity.ContentMetadata;
import com.streamnow.api.entity.ViewHistory;
import com.streamnow.api.repository.ContentMetadataRepository;
import com.streamnow.api.repository.ContentRatingRepository;
import com.streamnow.api.repository.ContentRepository;
import com.streamnow.api.repository.ViewHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ViewHistoryRepository watchHistoryRepository;
    private final ContentRatingRepository ratingRepository;
    private final ContentRepository contentRepository;
    private final ContentMetadataRepository metadataRepository;

    public List<Content> getPersonalizedRecommendations(Long userId) {
        // Step 1: Watched Content
        List<ViewHistory> viewHistory = watchHistoryRepository.findByUserId(userId);
        List<String> watchedContentIds = viewHistory.stream()
                .map(ViewHistory::getContentId)
                .distinct()
                .toList();

        if (watchedContentIds.isEmpty()) return List.of(); // nothing watched

        // Step 2: Extract genres from metadata
        Set<String> preferredGenres = metadataRepository.findByContentIdInAndMetaKey(watchedContentIds, "genre")
                .stream()
                .map(ContentMetadata::getMetaValue)
                .collect(Collectors.toSet());

        // Step 3: Find similar users (based on common content rated)
        List<Long> similarUsers = ratingRepository.findSimilarUsers(userId, watchedContentIds);

        if (similarUsers.isEmpty()) return List.of(); // no similar users

        // Step 4: Get top-rated content by similar users
        List<Content> similarUsersTopRatedContent = ratingRepository
                .findTopRatedContentByUsers(similarUsers, PageRequest.of(0, 10))
                .getContent();

        // Step 5: Filter content that’s not already watched and matches genres
        return similarUsersTopRatedContent.stream()
                .filter(content ->
                        !watchedContentIds.contains(content.getId()) &&
                                preferredGenres.stream().anyMatch(genre -> content.getGenre().contains(genre))
                )
                .collect(Collectors.toList());
    }

    public List<Content> getTrendingContent() {
        return contentRepository.findTrending(PageRequest.of(0, 10)).getContent();
    }
}
