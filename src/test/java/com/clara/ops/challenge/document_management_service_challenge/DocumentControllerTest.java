package com.clara.ops.challenge.document_management_service_challenge;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clara.ops.challenge.dto.DocumentDto;
import com.clara.ops.challenge.dto.DownloadDto;
import com.clara.ops.challenge.dto.FileDataDto;
import com.clara.ops.challenge.exception.ResourceNotFoundException;
import com.clara.ops.challenge.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DocumentService documentService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void uploadDocument_Success() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[] {1, 2, 3});
    MockMultipartFile uploadJson =
        new MockMultipartFile(
            "upload",
            "",
            "application/json",
            "{\"name\": \"test.pdf\", \"user\": \"ana\"}".getBytes(StandardCharsets.UTF_8));

    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/document-management/upload")
                .file(file)
                .file(uploadJson)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().string("The document was uploaded successfully. "));
  }

  @Test
  void searchDocuments_Success() throws Exception {
    DocumentDto document = new DocumentDto();
    document.setName("test.pdf");
    Page<DocumentDto> mockPage = new PageImpl<>(List.of(document));
    when(documentService.searchDocuments(any(FileDataDto.class), anyInt(), anyInt()))
        .thenReturn(mockPage);

    mockMvc
        .perform(
            post("/document-management/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FileDataDto())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("test.pdf"));
  }

  @Test
  void downloadDocument_Success() throws Exception {
    when(documentService.download(1L))
        .thenReturn(new DownloadDto("http://minio-file-url/test.pdf"));

    mockMvc
        .perform(get("/document-management/download/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.url").value("http://minio-file-url/test.pdf"));
  }

  @Test
  void downloadDocument_NotFound() throws Exception {
    when(documentService.download(1L))
        .thenThrow(new ResourceNotFoundException("Document not found"));

    mockMvc.perform(get("/document-management/download/1")).andExpect(status().isNotFound());
  }
}
