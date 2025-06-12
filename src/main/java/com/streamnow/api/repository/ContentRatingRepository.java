package com.streamnow.api.repository;

import com.streamnow.api.entity.Content;
import com.streamnow.api.entity.ContentRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentRatingRepository extends JpaRepository<ContentRating, Long> {
    Optional<ContentRating> findByUserIdAndContentId(Long userId, String contentId);

    @Query("SELECT AVG(cr.rating) FROM ContentRating cr WHERE cr.content.id = :contentId")
    double getAverageRatingByContentId(@Param("contentId") String contentId);

    @Query(value = """
  SELECT DISTINCT cr.user_id
  FROM content_rating cr
  WHERE cr.content_id IN :contentIds
    AND cr.user_id != :userId
  GROUP BY cr.user_id
  ORDER BY COUNT(cr.user_id) DESC
  LIMIT 5
""", nativeQuery = true)
    List<Long> findSimilarUsers(@Param("userId") Long userId, @Param("contentIds") List<String> contentIds);

    @Query("""
    SELECT cr.content
    FROM ContentRating cr 
    WHERE cr.user.id IN :userIds 
    AND cr.rating >= 4 
    GROUP BY cr.content 
    ORDER BY AVG(cr.rating) DESC""")
    Page<Content> findTopRatedContentByUsers(@Param("userIds") List<Long> userIds, Pageable pageable);

    List<ContentRating> findByUserId(Long userId);
}