package com.streamnow.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content")
public class Content {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "video_path")
    private String videoPath;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "video_url", length = 512, nullable = false)
    private String videoUrl;

    @Column(name = "thumbnail_url", length = 512)
    private String thumbnailUrl;

    @Column(columnDefinition = "JSON")
    private String genre;

    @Column(name = "release_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    private Double rating;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "duration")
    private Integer duration; // optional field

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @ElementCollection
    @CollectionTable(name = "content_categories", joinColumns = @JoinColumn(name = "content_id"))
    @Column(name = "category")
    private Set<String> categories;

    @ElementCollection
    @CollectionTable(name = "content_video_urls", joinColumns = @JoinColumn(name = "content_id"))
    @MapKeyColumn(name = "quality")
    @Column(name = "url")
    private Map<String, String> videoUrls;

    @ElementCollection
    @CollectionTable(name = "content_hls_manifests", joinColumns = @JoinColumn(name = "content_id"))
    @MapKeyColumn(name = "quality")
    @Column(name = "manifest_url")
    private Map<String, String> hlsManifests;

    public enum Type {
        MOVIE, TV_SHOW
    }
}
