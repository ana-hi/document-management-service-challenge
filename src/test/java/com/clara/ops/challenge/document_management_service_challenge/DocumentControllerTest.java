package com.clara.ops.challenge.document_management_service_challenge;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clara.ops.challenge.controller.DocumentController;
import com.clara.ops.challenge.dto.DownloadDto;
import com.clara.ops.challenge.exception.ResourceNotFoundException;
import com.clara.ops.challenge.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DocumentService documentService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void uploadDocument_Success() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[] {1, 2, 3});
    String uploadJson = "{\"name\": \"test.pdf\"}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/documents/upload")
                .file(file)
                .param("uploadJson", uploadJson))
        .andExpect(status().isOk());
  }

  @Test
  void searchDocuments_Success() throws Exception {
    mockMvc
        .perform(get("/documents/search").param("page", "0").param("size", "10"))
        .andExpect(status().isOk());
  }

  @Test
  void downloadDocument_Success() throws Exception {
    when(documentService.download(1L)).thenReturn(new DownloadDto("http://minio-url/test.pdf"));

    mockMvc
        .perform(get("/documents/download/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.url").value("http://minio-url/test.pdf"));
  }

  @Test
  void downloadDocument_NotFound() throws Exception {
    when(documentService.download(1L))
        .thenThrow(new ResourceNotFoundException("Document not found"));

    mockMvc.perform(get("/documents/download/1")).andExpect(status().isNotFound());
  }
}
