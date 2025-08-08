package com.okoth.mortgage.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.okoth.mortgage.models.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDto {
  private String id;

  @NotBlank(message = "File name is required")
  private String fileName;

  @NotBlank(message = "File type is required")
  private String fileType;

  @NotNull(message = "File size is required")
  @Positive(message = "File size must be positive")
  private Long fileSize;

  @NotBlank(message = "Presigned URL is required")
  private String presignedUrl;

  @NotNull(message = "Document type is required")
  private DocumentType documentType;
}
