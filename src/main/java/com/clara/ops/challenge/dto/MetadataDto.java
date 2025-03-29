package com.clara.ops.challenge.dto;

import lombok.Data;

@Data
public class MetadataDto {
  private Integer currentPage;
  private Integer itemsPerPage;
  private Integer currentItems;
  private Integer totalPages;
  private Integer totalItems;
}
