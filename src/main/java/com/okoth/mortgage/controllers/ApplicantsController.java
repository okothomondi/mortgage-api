package com.okoth.mortgage.controllers;

import com.okoth.mortgage.models.custom.AppResponseBody;
import com.okoth.mortgage.models.dto.ApplicationDto;
import com.okoth.mortgage.models.dto.ApplicationListResponse;
import com.okoth.mortgage.models.dto.ApplicationResponse;
import com.okoth.mortgage.models.enums.ApplicationStatus;
import com.okoth.mortgage.services.LoansService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('APPLICANT')")
@RequestMapping("/api/v1/application")
@Tag(name = "Loan Application", description = "Loan request operations for members")
public class ApplicantsController {
  private final LoansService loansService;

  @PostMapping("")
  public ResponseEntity<ApplicationResponse> createApplication(
      @Valid @RequestBody ApplicationDto applicationDto) {
    log.info("Creating application of applicant: {}", applicationDto);
    return ResponseEntity.ok(loansService.createApplication(applicationDto));
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
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime createdFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime createdTo) {
    log.info("Received request to get my applications");
    return ResponseEntity.ok(
        loansService.getAllApplications(
            page, size, sortBy, sortDir, status, createdFrom, createdTo));
  }

  @GetMapping("status")
  @Operation(summary = "Loan status", description = "Get the status of an application")
  public ResponseEntity<AppResponseBody> getLoanStatus() {
    log.info("Received request to get loan status");
    return ResponseEntity.ok(loansService.getStatus());
  }

  @PostMapping("repay")
  @Operation(summary = "repay for loan", description = "Make instalments to pay")
  public ResponseEntity<AppResponseBody> repayLoan(
      @RequestParam UUID id, @RequestParam Double amount) {
    log.info("Received request to repay loan with id: {} and amount: {}", id, amount);
    return ResponseEntity.ok(loansService.makePayment(id, amount));
  }
}
