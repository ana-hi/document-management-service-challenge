package com.clara.ops.challenge.controller;

import com.clara.ops.challenge.dto.DocumentDto;
import com.clara.ops.challenge.dto.DownloadDto;
import com.clara.ops.challenge.dto.FileDataDto;
import com.clara.ops.challenge.service.DocumentService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document-management")
public class DocumentController {

  private final DocumentService documentService;

  public DocumentController(DocumentService documentService) {
    this.documentService = documentService;
  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadDocument(
      @RequestPart("upload") String uploadJson, @RequestPart("file") MultipartFile file) {

    documentService.uploadDocument(uploadJson, file);
    return ResponseEntity.ok("The document was uploaded successfully. ");
  }

  @PostMapping("/search")
  public ResponseEntity<Page<DocumentDto>> searchDocument(
      @RequestBody FileDataDto fileDataDto,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(required = false) List<String> sort) {
    Page<DocumentDto> documents = documentService.searchDocuments(fileDataDto, page, size);
    return ResponseEntity.ok(documents);
  }

  @GetMapping("/download/{documentId}")
  public ResponseEntity<DownloadDto> downloadDocument(@PathVariable Long documentId) {
    DownloadDto downloadDto = documentService.download(documentId);
    return ResponseEntity.ok(downloadDto);
  }
}
