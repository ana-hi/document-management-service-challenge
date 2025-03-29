package com.clara.ops.challenge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tags", schema = "document_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tag_id")
  private Long id;

  private String tagName;

  @ManyToMany(mappedBy = "tags")
  private Set<DocumentEntity> documents;

  public TagEntity(String tagName) {
    this.tagName = tagName;
  }
}
