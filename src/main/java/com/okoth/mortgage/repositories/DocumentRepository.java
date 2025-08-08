package com.okoth.mortgage.repositories;

import com.okoth.mortgage.models.db.Document;
import com.okoth.mortgage.models.enums.DocumentType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

  List<Document> findByApplicationId(UUID applicationId);

  boolean existsByApplicationIdAndFileName(UUID applicationId, String fileName);

  @Modifying
  @Query("DELETE FROM Document d WHERE d.application.id = :applicationId")
  void deleteByApplicationId(UUID applicationId);

  long countByApplicationId(UUID applicationId);

  List<Document> findByApplicationIdAndDocumentType(UUID applicationId, DocumentType documentType);

  @Modifying
  @Query("UPDATE Document d SET d.presignedUrl = :newUrl WHERE d.id = :documentId")
  void updatePresignedUrl(UUID documentId, String newUrl);
}
