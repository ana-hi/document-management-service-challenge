package com.clara.ops.challenge.repository;

import com.clara.ops.challenge.entity.TagEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
  Optional<TagEntity> findByTagName(String name);
}
