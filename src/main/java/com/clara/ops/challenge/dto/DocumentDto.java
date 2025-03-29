package com.clara.ops.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto extends FileDataDto {
  private Long id;
  private Long size;
  private String type;
  private String createdAt;
}
