package com.clara.ops.challenge.repository;

import com.clara.ops.challenge.entity.DocumentEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

  @Query(
      "SELECT d FROM DocumentEntity d "
          + "LEFT JOIN d.tags t "
          + "WHERE (:user IS NULL OR d.user = :user) "
          + "AND (:fileName IS NULL OR d.fileName = :fileName) "
          + "AND (:tags IS NULL OR t.tagName IN :tags)")
  public Page<DocumentEntity> searchDocuments(
      @Param("user") String user,
      @Param("fileName") String fileName,
      @Param("tags") List<String> tags,
      Pageable pageable);
}
