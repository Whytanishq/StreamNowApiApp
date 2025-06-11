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

        @Query(value = "SELECT * FROM content WHERE JSON_CONTAINS(genre, :genre)", nativeQuery = true)
        Page<Content> filterByGenre(@Param("genre") String genre, Pageable pageable);

        @Query("SELECT c FROM Content c WHERE :genre MEMBER OF c.genre")
        Page<Content> findByGenreIn(@Param("genre") List<String> genres, Pageable pageable);

        Page<Content> findByGenreContaining(String genre, Pageable pageable);
        Page<Content> findByType(Content.Type type, Pageable pageable);
        Page<Content> findByGenreContainingAndType(String genre, Content.Type type, Pageable pageable);

        @Query("SELECT c FROM Content c ORDER BY c.viewCount DESC")
        Page<Content> findTrending(Pageable pageable);



    }
