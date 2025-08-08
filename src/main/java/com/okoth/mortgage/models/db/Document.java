package com.okoth.mortgage.models.db;

import com.okoth.mortgage.models.dto.DocumentDto;
import com.okoth.mortgage.models.enums.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String fileType;

  @Column(nullable = false)
  private Long fileSize;

  @Column(nullable = false)
  private String presignedUrl;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DocumentType documentType;

  @ManyToOne
  @JoinColumn(name = "application_id", nullable = false)
  private Application application;

  public Document(DocumentDto documentDto, Application application) {
    this.presignedUrl = documentDto.getPresignedUrl();
    this.documentType = documentDto.getDocumentType();
    this.fileName = documentDto.getFileName();
    this.fileType = documentDto.getFileType();
    this.fileSize = documentDto.getFileSize();
    this.documentType = documentDto.getDocumentType();
    this.application = application;
  }
}
