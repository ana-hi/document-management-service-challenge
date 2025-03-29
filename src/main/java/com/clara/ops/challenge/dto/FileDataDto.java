package com.clara.ops.challenge.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDataDto {
  private String user;
  private String name;
  private List<String> tags;
}
