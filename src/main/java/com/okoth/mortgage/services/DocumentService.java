package com.okoth.mortgage.services;

import com.okoth.mortgage.exceptions.ResourceNotFoundException;
import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.db.Document;
import com.okoth.mortgage.models.dto.DocumentDto;
import com.okoth.mortgage.repositories.DocumentRepository;
import com.okoth.mortgage.services.awsS3.S3Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

  private final S3Service s3Service;
  private final DocumentRepository documentRepository;

  @Transactional
  public Document createDocument(DocumentDto documentDto, Application application) {
    Document document = new Document(documentDto, application);
    try {
      String presignedUrl = s3Service.generatePresignedDownloadUrl(documentDto.getFileName());
      document.setPresignedUrl(presignedUrl);
    } catch (Exception e) {
      log.error("Failed to generatePresignedDownloadUrl({}) : {}", documentDto, e.getMessage());
      document.setPresignedUrl(e.getMessage());
    }
    return documentRepository.save(document);
  }

  @Transactional(readOnly = true)
  public List<DocumentDto> getDocumentsByApplicationId(UUID applicationId) {
    List<Document> documents = documentRepository.findByApplicationId(applicationId);
    return documents.stream().map(this::convertToDto).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public DocumentDto getDocumentById(UUID documentId) {
    Document document =
        documentRepository
            .findById(documentId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Document not found with id: " + documentId));
    return convertToDto(document);
  }

  @Transactional
  public void deleteDocument(UUID documentId) {
    Document document =
        documentRepository
            .findById(documentId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Document not found with id: " + documentId));
    s3Service.deleteFile(document.getFileName());
    documentRepository.delete(document);
  }

  @Transactional
  public void deleteAllDocumentsForApplication(UUID applicationId) {
    List<Document> documents = documentRepository.findByApplicationId(applicationId);
    documents.forEach(document -> s3Service.deleteFile(document.getFileName()));
    documentRepository.deleteAll(documents);
  }

  private DocumentDto convertToDto(Document document) {
    return DocumentDto.builder()
        .id(document.getId().toString())
        .fileName(document.getFileName())
        .fileType(document.getFileType())
        .fileSize(document.getFileSize())
        .presignedUrl(document.getPresignedUrl())
        .documentType(document.getDocumentType())
        .build();
  }
}
