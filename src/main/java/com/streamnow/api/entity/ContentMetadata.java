package com.streamnow.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "content_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false)
    private String contentId; // Reference to Content.id

    @Column(name = "meta_key", nullable = false)
    private String metaKey; // e.g., "genre", "language"

    @Column(name = "meta_value", nullable = false)
    private String metaValue; // e.g., "Action", "Hindi"
}
