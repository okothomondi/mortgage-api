package com.okoth.mortgage.controllers;

import com.okoth.mortgage.models.dto.ApplicationDto;
import com.okoth.mortgage.models.dto.ApplicationListResponse;
import com.okoth.mortgage.models.dto.ApplicationResponse;
import com.okoth.mortgage.models.dto.DecisionDto;
import com.okoth.mortgage.models.enums.ApplicationStatus;
import com.okoth.mortgage.services.LoansService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('OFFICER')")
@RequestMapping("/api/v1/approval")
@Tag(name = "Loan Approval", description = "Authorization operations for loans Approve/Reject")
public class OfficersController {
  private final LoansService loansService;

  @PostMapping("/{nationalId}")
  public ResponseEntity<ApplicationResponse> createApplication(
      @PathVariable String nationalId, @Valid @RequestBody ApplicationDto applicationDto) {
    log.info("Creating application for officer: {}", applicationDto);
    return ResponseEntity.ok(loansService.createApplication(nationalId, applicationDto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable UUID id) {
    log.info("Getting application with id: {}", id);
    return ResponseEntity.ok(loansService.getApplicationById(id));
  }

  @GetMapping
  public ResponseEntity<ApplicationListResponse> getAllApplications(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDir,
      @RequestParam(required = false) ApplicationStatus status,
      @RequestParam(required = false) String nationalId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime createdFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime createdTo) {
    return ResponseEntity.ok(
        loansService.getAllApplications(
            page, size, sortBy, sortDir, status, nationalId, createdFrom, createdTo));
  }

  @PatchMapping("/{id}/decision")
  public ResponseEntity<ApplicationResponse> updateApplicationDecision(
      @PathVariable UUID id, @Valid @RequestBody DecisionDto decisionDto) {
    return ResponseEntity.ok(loansService.updateApplicationDecision(id, decisionDto));
  }
}
