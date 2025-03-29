package com.clara.ops.challenge.mapper;

import com.clara.ops.challenge.dto.DocumentDto;
import com.clara.ops.challenge.dto.FileDataDto;
import com.clara.ops.challenge.entity.DocumentEntity;
import com.clara.ops.challenge.entity.TagEntity;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class DocumentMapper {

  public DocumentEntity toEntity(FileDataDto uploadDto, MultipartFile file, String url) {
    DocumentEntity document = new DocumentEntity();
    document.setUser(uploadDto.getUser());
    document.setFileName(uploadDto.getName());
    document.setUser(uploadDto.getUser());
    document.setTags(
        uploadDto.getTags().stream().map(name -> new TagEntity(name)).collect(Collectors.toList()));
    document.setFileSize(file.getSize());
    document.setFileType(file.getContentType());
    document.setMinIoPath(url.substring(0, 200));
    document.setCreatedAt(LocalDateTime.now());
    return document;
  }

  public DocumentDto toDto(DocumentEntity entity) {
    if (entity == null) return null;

    DocumentDto documentDto = new DocumentDto();
    documentDto.setUser(entity.getUser());
    documentDto.setName(entity.getFileName());
    documentDto.setTags(
        entity.getTags().stream().map(TagEntity::getTagName).collect(Collectors.toList()));
    documentDto.setId(entity.getId());
    documentDto.setCreatedAt(entity.getCreatedAt().toString());
    documentDto.setSize(entity.getFileSize());
    documentDto.setType(entity.getFileType());

    return documentDto;
  }
}
