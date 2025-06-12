package com.streamnow.api.repository;

import com.streamnow.api.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, String> {

    @Query("SELECT c FROM Content c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Content> searchByTitle(@Param("query") String query, Pageable pageable);

    @Query(value = """
    SELECT * FROM content c
    WHERE EXISTS (
        SELECT 1 FROM JSON_TABLE(c.genre, '$[*]' COLUMNS (genre VARCHAR(255) PATH '$')) AS jt
        WHERE jt.genre IN (:genres)
    )
""", nativeQuery = true)
    Page<Content> filterByGenre(@Param("genres") List<String> genres, Pageable pageable);


    Page<Content> findByGenreContaining(String genre, Pageable pageable);
    Page<Content> findByType(Content.Type type, Pageable pageable);
    Page<Content> findByGenreContainingAndType(String genre, Content.Type type, Pageable pageable);

    @Query("SELECT c FROM Content c ORDER BY c.viewCount DESC")
    Page<Content> findTrending(Pageable pageable);

    @Query("SELECT AVG(c.rating) FROM Content c")
    double getAverageRating();

    long countByType(Content.Type type);

    @Query(value = """
        SELECT jt.genre as genre, COUNT(*) as count
        FROM content,
        JSON_TABLE(genre, '$[*]' COLUMNS (genre VARCHAR(255) PATH '$')) AS jt
        GROUP BY jt.genre
    """, nativeQuery = true)
    List<GenreCount> getGenreDistribution();

    interface GenreCount {
        String getGenre();
        Long getCount();
    }
}
