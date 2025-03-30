package com.clara.ops.challenge.document_management_service_challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories("com.clara.ops.challenge.repository")
@EntityScan("com.clara.ops.challenge.entity")
@ComponentScan("com.clara.ops.challenge")
@EnableAsync
public class DocumentManagementServiceChallengeApplication {

  public static void main(String[] args) {
    SpringApplication.run(DocumentManagementServiceChallengeApplication.class, args);
  }
}
