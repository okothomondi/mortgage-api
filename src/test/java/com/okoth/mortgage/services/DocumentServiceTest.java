package com.okoth.mortgage.services;

import static com.okoth.mortgage.models.enums.DocumentType.ID_PROOF;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.okoth.mortgage.exceptions.ResourceNotFoundException;
import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.db.Document;
import com.okoth.mortgage.models.dto.DocumentDto;
import com.okoth.mortgage.repositories.DocumentRepository;
import com.okoth.mortgage.services.awsS3.S3Service;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock private S3Service s3Service;
  @Mock private DocumentRepository documentRepository;

  @InjectMocks private DocumentService documentService;

  private DocumentDto documentDto;
  private Application application;
  private Document document;

  @BeforeEach
  void setUp() {
    application = new Application();
    documentDto =
        DocumentDto.builder()
            .fileName("test.pdf")
            .fileType("application/pdf")
            .fileSize(12345L)
            .documentType(ID_PROOF)
            .build();

    document = new Document(documentDto, application);
    document.setPresignedUrl("presignedUrlFromS3");
  }

  @Test
  void testCreateDocument() {
    when(documentRepository.save(any(Document.class))).thenReturn(document);

    Document saved = documentService.createDocument(documentDto, application);

    assertNotNull(saved);
    assertEquals("presignedUrlFromS3", saved.getPresignedUrl());
    verify(documentRepository).save(any(Document.class));
  }

  @Test
  void testGetDocumentsByApplicationId() {
    UUID appId = UUID.randomUUID();
    List<Document> docs = List.of(document);
    when(documentRepository.findByApplicationId(appId)).thenReturn(docs);

    List<DocumentDto> result = documentService.getDocumentsByApplicationId(appId);

    assertEquals(1, result.size());
    assertEquals("test.pdf", result.get(0).getFileName());
  }

  @Test
  void testGetDocumentByIdSuccess() {
    UUID docId = UUID.randomUUID();
    document.setId(docId);
    when(documentRepository.findById(docId)).thenReturn(Optional.of(document));

    DocumentDto result = documentService.getDocumentById(docId);

    assertNotNull(result);
    assertEquals("test.pdf", result.getFileName());
    verify(documentRepository).findById(docId);
  }

  @Test
  void testGetDocumentByIdThrowsNotFound() {
    UUID docId = UUID.randomUUID();
    when(documentRepository.findById(docId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          documentService.getDocumentById(docId);
        });
  }

  @Test
  void testDeleteDocumentSuccess() {
    UUID docId = UUID.randomUUID();
    document.setId(docId);
    when(documentRepository.findById(docId)).thenReturn(Optional.of(document));

    documentService.deleteDocument(docId);

    verify(s3Service).deleteFile("test.pdf");
    verify(documentRepository).delete(document);
  }

  @Test
  void testDeleteDocumentThrowsNotFound() {
    UUID docId = UUID.randomUUID();
    when(documentRepository.findById(docId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          documentService.deleteDocument(docId);
        });

    verify(documentRepository, never()).delete(any());
    verify(s3Service, never()).deleteFile(anyString());
  }

  @Test
  void testDeleteAllDocumentsForApplication() {
    UUID appId = UUID.randomUUID();
    List<Document> docs = List.of(document);
    when(documentRepository.findByApplicationId(appId)).thenReturn(docs);

    documentService.deleteAllDocumentsForApplication(appId);

    verify(s3Service).deleteFile("test.pdf");
    verify(documentRepository).deleteAll(docs);
  }
}
