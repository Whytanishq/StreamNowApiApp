package com.streamnow.api.service;

import com.streamnow.api.entity.Content;
import com.streamnow.api.entity.ContentRating;
import com.streamnow.api.entity.User;
import com.streamnow.api.exception.ResourceNotFoundException;
import com.streamnow.api.repository.ContentRatingRepository;
import com.streamnow.api.repository.ContentRepository;
import com.streamnow.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ContentRatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public void rateContent(Long userId, String contentId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        ContentRating contentRating = ratingRepository.findByUserIdAndContentId(userId, contentId)
                .orElseGet(() -> ContentRating.builder()
                        .user(user)
                        .content(content)
                        .build());

        contentRating.setRating(rating);
        ratingRepository.save(contentRating);

        // Update content's average rating
        updateContentAverageRating(contentId);
    }

    private void updateContentAverageRating(String contentId) {
        Double averageRating = ratingRepository.getAverageRatingByContentId(contentId);
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
        content.setRating(averageRating);
        contentRepository.save(content);
    }

    public Double getUserRating(Long userId, String contentId) {
        return ratingRepository.findByUserIdAndContentId(userId, contentId)
                .map(ContentRating::getRating)
                .map(Integer::doubleValue)
                .orElse(null);
    }

}