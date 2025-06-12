package com.streamnow.api.service;

import com.streamnow.api.dto.ContentDto;
import com.streamnow.api.entity.Content;
import com.streamnow.api.entity.ViewHistory;
import com.streamnow.api.repository.ContentRepository;
import com.streamnow.api.repository.ViewHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewHistoryService {
    private final ViewHistoryRepository viewHistoryRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public void recordView(Long userId, String contentId, int durationMinutes) {
        ViewHistory existing = viewHistoryRepository.findByUserIdAndContentId(userId, contentId)
                .orElse(null);

        if (existing != null) {
            existing.setProgress(durationMinutes);
            viewHistoryRepository.save(existing);
        } else {
            ViewHistory newEntry = ViewHistory.builder()
                    .userId(userId)
                    .contentId(contentId)
                    .progress(durationMinutes)
                    .totalDuration(getContentDuration(contentId))
                    .build();
            viewHistoryRepository.save(newEntry);
        }
    }

    public List<ContentDto> getContinueWatching(Long userId) {
        return viewHistoryRepository.findByUserIdAndProgressLessThan(userId, 100).stream()
                .map(view -> contentRepository.findById(view.getContentId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private int getContentDuration(String contentId) {
        return contentRepository.findById(contentId)
                .map(Content::getDuration)
                .orElse(120);
    }


    private ContentDto mapToDto(Content content) {
        return ContentDto.fromEntity(content);
    }

}