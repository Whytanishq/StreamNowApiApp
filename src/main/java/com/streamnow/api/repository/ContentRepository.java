package com.streamnow.api.repository;

import com.streamnow.api.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, String> {

    @Query("SELECT c FROM Content c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Content> searchByTitle(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM content WHERE JSON_CONTAINS(genre, :genre)", nativeQuery = true)
    Page<Content> filterByGenre(@Param("genre") String genre, Pageable pageable);
}
