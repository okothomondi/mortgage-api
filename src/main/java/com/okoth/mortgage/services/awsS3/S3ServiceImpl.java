package com.okoth.mortgage.services.awsS3;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  private final S3Client s3Client;
  private final S3Presigner presigner;

  @Override
  public String generatePresignedUploadUrl(String fileName, String contentType) {
    PutObjectRequest objectRequest =
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(contentType)
            .build();

    PresignedPutObjectRequest presignedRequest =
        presigner.presignPutObject(
            r -> r.signatureDuration(Duration.ofMinutes(10)).putObjectRequest(objectRequest));
    return presignedRequest.url().toString();
  }

  @Override
  public String generatePresignedDownloadUrl(String fileName) {
    try {
      GetObjectRequest getObjectRequest =
          GetObjectRequest.builder().bucket(bucketName).key(fileName).build();
      PresignedGetObjectRequest presignedRequest =
          presigner.presignGetObject(
              r -> r.signatureDuration(Duration.ofMinutes(5)).getObjectRequest(getObjectRequest));
      return presignedRequest.url().toString();
    } catch (S3Exception e) {
      log.error("Error generatePresignedDownloadUrl({}) : {}", fileName, e.getMessage());
      return "S3Exception";
    }
  }

  @Override
  public void deleteFile(String fileName) {
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build());
  }

  @Override
  public String getFileUrl(String fileName) {
    return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
  }

  @Override
  public boolean doesFileExist(String fileName) {
    try {
      s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(fileName).build());
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    }
  }
}
