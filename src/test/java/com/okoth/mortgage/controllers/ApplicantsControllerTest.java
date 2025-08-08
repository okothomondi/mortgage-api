package com.okoth.mortgage.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okoth.mortgage.models.dto.ApplicationDto;
import com.okoth.mortgage.models.dto.ApplicationResponse;
import com.okoth.mortgage.services.LoansService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicantsControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @InjectMocks private LoansService loansService;

  @Test
  void testCreateApplicationWithValidData() throws Exception {
    ApplicationDto applicationDto =
        ApplicationDto.builder().loanAmount(50000.0).loanTermYears(5).annualIncome(60000.0).build();

    ApplicationResponse response = new ApplicationResponse();

    when(loansService.createApplication(Mockito.any(ApplicationDto.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists());

    verify(loansService, times(1)).createApplication(Mockito.any(ApplicationDto.class));
  }

  @Test
  void testCreateApplicationWithInvalidLoanAmount() throws Exception {
    ApplicationDto applicationDto =
        ApplicationDto.builder()
            .loanAmount(-50000.0)
            .loanTermYears(5)
            .annualIncome(60000.0)
            .build();

    mockMvc
        .perform(
            post("/api/v1/application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors").exists());
  }

  @Test
  void testCreateApplicationWithMissingFields() throws Exception {
    ApplicationDto applicationDto = ApplicationDto.builder().loanTermYears(5).build();

    mockMvc
        .perform(
            post("/api/v1/application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors").exists());
  }

  @Test
  void testGetApplicationByIdWithValidId() throws Exception {
    UUID validId = UUID.randomUUID();
    ApplicationResponse response = new ApplicationResponse();

    when(loansService.getApplicationById(validId)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/application/{id}", validId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists());

    verify(loansService, times(1)).getApplicationById(validId);
  }

  @Test
  void testGetApplicationByIdWithInvalidId() throws Exception {
    UUID invalidId = UUID.randomUUID();
    when(loansService.getApplicationById(invalidId))
        .thenThrow(new IllegalArgumentException("Invalid ID"));
    mockMvc
        .perform(get("/api/v1/application/{id}", invalidId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
