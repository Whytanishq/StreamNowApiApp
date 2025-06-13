package com.streamnow.api.repository;

import com.streamnow.api.entity.ContentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentMetadataRepository extends JpaRepository<ContentMetadata, Long> {

    List<ContentMetadata> findByContentIdInAndMetaKey(List<String> contentIds, String metaKey);
}
