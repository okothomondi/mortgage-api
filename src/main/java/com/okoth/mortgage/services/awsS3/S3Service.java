package com.okoth.mortgage.services.awsS3;

public interface S3Service {
  String generatePresignedUploadUrl(String fileName, String contentType);

  String generatePresignedDownloadUrl(String fileName);

  void deleteFile(String fileName);

  String getFileUrl(String fileName);

  boolean doesFileExist(String fileName);
}
