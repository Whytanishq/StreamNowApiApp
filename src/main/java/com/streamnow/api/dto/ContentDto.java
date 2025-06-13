package com.streamnow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamnow.api.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {
    private String id;
    private String title;
    private String description;
    private List<String> genre;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String thumbnailUrl;
    private String videoUrl;
    private Content.Type type;
    private Double rating;
    private Set<String> categories;

    private Integer durationMinutes;
    private Integer releaseYear;
    private String categoryId;

    public static ContentDto fromEntity(Content content) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> genreList;
        try {
            genreList = objectMapper.readValue(content.getGenre(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize genre JSON", e);
        }

        return ContentDto.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .genre(genreList)
                .releaseDate(content.getReleaseDate())
                .thumbnailUrl(content.getThumbnailUrl())
                .videoUrl(content.getVideoUrl())
                .type(content.getType())
                .rating(content.getRating())
                .categories(content.getCategories())
                .durationMinutes(content.getDurationMinutes())
                .releaseYear(content.getReleaseYear())
                .categoryId(content.getCategoryId())
                .build();
    }
}
