package com.streamnow.api.repository;

import com.streamnow.api.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {
    Optional<ViewHistory> findByUserIdAndContentId(Long userId, String contentId);
    List<ViewHistory> findByUserIdAndProgressLessThan(Long userId, int progress);
    List<ViewHistory> findByUserId(Long userId);
    List<ViewHistory> findByUserIdAndIsCompletedFalseOrderByLastWatchedAtDesc(Long userId);

}
