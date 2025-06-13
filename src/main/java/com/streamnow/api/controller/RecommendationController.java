package com.streamnow.api.controller;

import com.streamnow.api.entity.Content;
import com.streamnow.api.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/personalized/{userId}")
    public List<Content> getPersonalized(@PathVariable Long userId) {
        return recommendationService.getPersonalizedRecommendations(userId);
    }

    @GetMapping("/trending")
    public List<Content> getTrending() {
        return recommendationService.getTrendingContent();
    }
}
