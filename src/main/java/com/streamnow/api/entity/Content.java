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

    // Stored as JSON string
    @Column(columnDefinition = "JSON")
    private String genre;

    @Column(name = "release_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @Column(name = "thumbnail_url", length = 512)
    private String thumbnailUrl;

    @Column(name = "video_url", length = 512, nullable = false)
    private String videoUrl;

    @Column(name = "view_count")
    private Long viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    private Double rating;

    // ===== Added duration field (in minutes) =====
    @Column(name = "duration")
    private Integer duration;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    // Content.java entity
    @ElementCollection
    @CollectionTable(name = "content_categories", joinColumns = @JoinColumn(name = "content_id"))
    @Column(name = "category")
    private Set<String> categories;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public enum Type {
        MOVIE, TV_SHOW
    }
}
