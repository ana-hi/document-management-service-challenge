package com.clara.ops.challenge.document_management_service_challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.clara.ops.challenge.client.MinIOService;
import com.clara.ops.challenge.dto.DocumentDto;
import com.clara.ops.challenge.dto.DownloadDto;
import com.clara.ops.challenge.dto.FileDataDto;
import com.clara.ops.challenge.entity.DocumentEntity;
import com.clara.ops.challenge.exception.BadRequestException;
import com.clara.ops.challenge.exception.ResourceNotFoundException;
import com.clara.ops.challenge.mapper.DocumentMapper;
import com.clara.ops.challenge.repository.DocumentRepository;
import com.clara.ops.challenge.service.DocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class DocumentServiceTest {

  @Mock private MinIOService minioService;

  @Mock private DocumentRepository documentRepository;

  @Mock private ObjectMapper objectMapper;

  @Mock private DocumentMapper documentMapper;

  @InjectMocks private DocumentService documentService;

  @Test
  void uploadDocument_Success() throws Exception {
    String uploadJson =
        "{\"name\": \"test.pdf\", \"user\": \"ana\", \"tags\": [\"math\", \"tech\"]]}";
    MultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[] {1, 2, 3});

    FileDataDto fileDataDto = new FileDataDto();
    fileDataDto.setName("test.pdf");
    fileDataDto.setUser("ana");
    fileDataDto.setTags(Arrays.asList("math", "tech"));

    DocumentEntity documentEntity = new DocumentEntity();
    documentEntity.setFileName("test.pdf");
    documentEntity.setUser("ana");

    when(objectMapper.readValue(uploadJson, FileDataDto.class)).thenReturn(fileDataDto);
    when(minioService.uploadDocument(fileDataDto.getName(), file))
        .thenReturn(CompletableFuture.completedFuture("http://minio-file-url/test.pdf"));
    when(minioService.getPresignedUrl(fileDataDto.getName()))
        .thenReturn("http://minio-file-url/test.pdf");
    when(documentMapper.toEntity(fileDataDto, file, "http://minio-file-url/test.pdf"))
        .thenReturn(documentEntity);

    documentService.uploadDocument(uploadJson, file);

    verify(documentRepository, times(1)).save(documentEntity);
  }

  @Test
  void uploadDocument_InvalidJson() throws Exception {
    String invalidJson = "invalid json";
    MultipartFile file = new MockMultipartFile("file", new byte[] {});

    when(objectMapper.readValue(invalidJson, FileDataDto.class))
        .thenThrow(JsonProcessingException.class);

    assertThrows(
        BadRequestException.class, () -> documentService.uploadDocument(invalidJson, file));
  }

  @Test
  void searchDocuments_Success() {
    FileDataDto fileDataDto = new FileDataDto();
    fileDataDto.setUser("user1");
    fileDataDto.setName("doc");
    fileDataDto.setTags(List.of("tag1"));

    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    Page<DocumentEntity> documentEntities = new PageImpl<>(List.of(new DocumentEntity()));

    when(documentRepository.searchDocuments("user1", "doc", List.of("tag1"), pageable))
        .thenReturn(documentEntities);

    Page<DocumentDto> result = documentService.searchDocuments(fileDataDto, 0, 10);

    assertNotNull(result);
    verify(documentRepository, times(1)).searchDocuments("user1", "doc", List.of("tag1"), pageable);
  }

  @Test
  void downloadDocument_Success() {
    DocumentEntity document = new DocumentEntity();
    document.setMinIoPath("http://minio-file-url/test.pdf");

    when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

    DownloadDto result = documentService.download(1L);

    assertNotNull(result);
    assertEquals("http://minio-file-url/test.pdf", result.getUrl());
  }

  @Test
  void downloadDocument_NotFound() {
    when(documentRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> documentService.download(1L));
  }
}
