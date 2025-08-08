package com.okoth.mortgage.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.requests.ApiError;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationListResponse {
  private List<ApplicationDto> applications;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private List<ApiError> errors;
}
