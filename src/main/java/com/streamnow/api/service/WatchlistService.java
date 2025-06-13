package com.streamnow.api.service;

import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.dto.WatchlistDto;
import com.streamnow.api.entity.Content;
import com.streamnow.api.entity.User;
import com.streamnow.api.entity.ViewHistory;
import com.streamnow.api.entity.Watchlist;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.ContentRepository;
import com.streamnow.api.repository.UserRepository;
import com.streamnow.api.repository.ViewHistoryRepository;
import com.streamnow.api.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final ViewHistoryRepository watchHistoryRepository;
    private final ContentService contentService;

    @Transactional
    public WatchlistDto addToWatchlist(Long userId, String contentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!contentRepository.existsById(contentId)) {
            throw new ResourceNotFoundException("Content not found");
        }

        if (watchlistRepository.existsByUserAndContentId(user, contentId)) {
            throw new IllegalArgumentException("Content already in watchlist");
        }

        Watchlist watchlist = Watchlist.builder()
                .user(user)
                .contentId(contentId)
                .build();

        Watchlist saved = watchlistRepository.save(watchlist);
        return mapToDto(saved);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, String contentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        watchlistRepository.deleteByUserAndContentId(user, contentId);
    }

    @Transactional
    public void updateWatchProgress(Long userId, String contentId, int progressMinutes) {
        Optional<ViewHistory> existing = watchHistoryRepository.findByUserIdAndContentId(userId, contentId);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        ViewHistory history = existing.orElseGet(ViewHistory::new);
        history.setUserId(userId);
        history.setContentId(contentId);
        history.setProgress(progressMinutes);
        history.setTotalDuration(content.getDurationMinutes());
        history.setLastWatchedAt(LocalDateTime.now());
        history.setCompleted(progressMinutes >= content.getDurationMinutes());

        watchHistoryRepository.save(history);
    }

    public List<ContentDto> getUserWatchlist(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return watchlistRepository.findByUser(user).stream()
                .map(w -> contentService.getContentById(w.getContentId()))
                .collect(Collectors.toList());
    }

    public List<Content> getContinueWatching(Long userId) {
        return watchHistoryRepository.findByUserIdAndIsCompletedFalseOrderByLastWatchedAtDesc(userId).stream()
                .map(history -> contentRepository.findById(history.getContentId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private WatchlistDto mapToDto(Watchlist watchlist) {
        return WatchlistDto.builder()
                .id(watchlist.getId())
                .userId(watchlist.getUser().getId())
                .contentId(watchlist.getContentId())
                .createdAt(watchlist.getCreatedAt().toString())
                .build();
    }
}
