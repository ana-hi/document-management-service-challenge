package com.clara.ops.challenge.client;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MinIOService {
  private MinioClient minioClient;

  @Value("${minio.url}")
  private String endpoint;

  @Value("${minio.access-key}")
  private String accessKey;

  @Value("${minio.secret-key}")
  private String secretKey;

  @Value("${minio.bucket-name}")
  private String bucketName;

  public CompletableFuture<String> uploadDocument(String documentName, MultipartFile file) {
    return CompletableFuture.supplyAsync(
        () -> {
          try (InputStream inputStream = file.getInputStream()) {
            minioClient =
                MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();

            minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(documentName).stream(
                        inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return documentName;

          } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
          }
        });
  }

  public String getPresignedUrl(String documentName) {
    try {
      minioClient =
          MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
      String url =
          minioClient.getPresignedObjectUrl(
              GetPresignedObjectUrlArgs.builder()
                  .bucket(bucketName)
                  .object(documentName)
                  .method(Method.GET)
                  .build());
      return url;

    } catch (Exception e) {
      throw new RuntimeException("Error getting presigned url", e);
    }
  }
}
