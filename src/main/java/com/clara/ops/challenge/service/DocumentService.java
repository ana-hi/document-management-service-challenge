package com.clara.ops.challenge.service;

import com.clara.ops.challenge.client.MinIOService;
import com.clara.ops.challenge.dto.DocumentDto;
import com.clara.ops.challenge.dto.DownloadDto;
import com.clara.ops.challenge.dto.FileDataDto;
import com.clara.ops.challenge.entity.DocumentEntity;
import com.clara.ops.challenge.exception.BadRequestException;
import com.clara.ops.challenge.exception.DatabaseException;
import com.clara.ops.challenge.exception.FileUploadException;
import com.clara.ops.challenge.exception.GeneralServiceException;
import com.clara.ops.challenge.exception.InternalServerException;
import com.clara.ops.challenge.exception.ResourceNotFoundException;
import com.clara.ops.challenge.exception.StorageException;
import com.clara.ops.challenge.mapper.DocumentMapper;
import com.clara.ops.challenge.repository.DocumentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {
  private final MinIOService minioService;
  private final DocumentRepository documentRepository;
  private final ObjectMapper objectMapper;
  private final DocumentMapper documentMapper;

  public DocumentService(
      MinIOService minioService,
      DocumentRepository documentRepository,
      ObjectMapper objectMapper,
      DocumentMapper documentMapper) {
    this.minioService = minioService;
    this.documentRepository = documentRepository;
    this.objectMapper = objectMapper;
    this.documentMapper = documentMapper;
  }

  @Async
  public void uploadDocument(String uploadJson, MultipartFile file) {
    try {
      FileDataDto uploadDto = objectMapper.readValue(uploadJson, FileDataDto.class);
      CompletableFuture<String> uploadFuture =
          minioService
              .uploadDocument(uploadDto.getName(), file)
              .exceptionally(
                  ex -> {
                    throw new StorageException("Failed to upload file to MinIO", ex);
                  });

      uploadFuture.join();
      String url = minioService.getPresignedUrl(uploadDto.getName());
      DocumentEntity document = documentMapper.toEntity(uploadDto, file, url);

      documentRepository.save(document);

    } catch (JsonProcessingException e) {
      throw new BadRequestException("Invalid JSON format", e);
    } catch (StorageException e) {
      throw new FileUploadException("Error uploading file", e);
    } catch (DataAccessException e) {
      throw new DatabaseException("Error saving document in database", e);
    } catch (Exception e) {
      throw new GeneralServiceException("Unexpected error during file upload", e);
    }
  }

  public Page<DocumentDto> searchDocuments(FileDataDto fileDataDto, Integer page, Integer size) {
    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      Page<DocumentEntity> documentPage =
          documentRepository.searchDocuments(
              fileDataDto.getUser(), fileDataDto.getName(), fileDataDto.getTags(), pageable);
      Page<DocumentDto> pageSearch = documentPage.map(documentMapper::toDto);
      return pageSearch;
    } catch (Exception e) {
      throw new InternalServerException("Error while searching documents", e);
    }
  }

  public DownloadDto download(Long documentId) {
    DocumentEntity document =
        documentRepository
            .findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

    return new DownloadDto(document.getMinIoPath());
  }
}
