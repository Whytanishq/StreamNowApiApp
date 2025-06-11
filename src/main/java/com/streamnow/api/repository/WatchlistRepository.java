package com.streamnow.api.repository;

import com.streamnow.api.entity.User;
import com.streamnow.api.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUser(User user);
    boolean existsByUserAndContentId(User user, String contentId);
    void deleteByUserAndContentId(User user, String contentId);

    @Query("SELECT DISTINCT c.genre FROM Content c " +
            "JOIN Watchlist w ON c.id = w.content.id " +
            "WHERE w.user.id = :userId")
    List<String> findTopGenresByUser(@Param("userId") Long userId);
}