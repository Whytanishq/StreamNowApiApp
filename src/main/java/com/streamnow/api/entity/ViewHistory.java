package com.streamnow.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "view_history")
public class ViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content_id", nullable = false)
    private String contentId;

    @Column(nullable = false)
    private int progress; // in minutes

    @Column(nullable = false)
    private int totalDuration; // in minutes

    @Column(name = "last_watched_at", nullable = false)
    private LocalDateTime lastWatchedAt;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;
}
