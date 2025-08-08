package com.okoth.mortgage.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AswS3Config {
  @Bean
  public S3Client s3Client() {
    return S3Client.builder().region(Region.AF_SOUTH_1).build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    return S3Presigner.builder().region(Region.AF_SOUTH_1).build();
  }
}
