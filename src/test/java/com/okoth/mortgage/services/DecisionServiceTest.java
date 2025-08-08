package com.okoth.mortgage.services;

import static com.okoth.mortgage.models.enums.DecisionStatus.APPROVED;
import static com.okoth.mortgage.models.enums.DecisionStatus.REJECTED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.db.Decision;
import com.okoth.mortgage.models.dto.DecisionDto;
import com.okoth.mortgage.repositories.DecisionRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DecisionServiceTest {

  @Mock private DecisionRepository decisionRepository;

  @InjectMocks private DecisionService decisionService;

  private Application application;
  private DecisionDto decisionDto;

  @BeforeEach
  void setup() {
    application = new Application();
    decisionDto = DecisionDto.builder().status(APPROVED).comments("Looks good").build();
  }

  @Test
  void shouldCreateDecisionSuccessfully() {
    // Given
    String approverId = "admin123";
    Decision savedDecision = new Decision(decisionDto);
    savedDecision.setApplication(application);
    savedDecision.setApproverId(approverId);

    when(decisionRepository.save(any(Decision.class))).thenReturn(savedDecision);

    // When
    Decision result = decisionService.createDecision(decisionDto, application, approverId);

    // Then
    assertNotNull(result);
    assertEquals(approverId, result.getApproverId());
    assertEquals(application, result.getApplication());
    assertEquals(APPROVED, result.getStatus());

    verify(decisionRepository).save(any(Decision.class));
  }

  @Test
  void shouldReturnDecisionDtoByApplicationId() {
    // Given
    UUID appId = UUID.randomUUID();
    Decision decision = new Decision();
    decision.setId(UUID.randomUUID());
    decision.setStatus(REJECTED);
    decision.setComments("Incomplete docs");
    decision.setApproverId("approver456");
    decision.setDecisionDate(LocalDateTime.now());

    when(decisionRepository.findByApplicationId(appId)).thenReturn(Optional.of(decision));

    // When
    DecisionDto dto = decisionService.getDecisionByApplicationId(appId.toString());

    // Then
    assertNotNull(dto);
    assertEquals(REJECTED, dto.getStatus());
    assertEquals("Incomplete docs", dto.getComments());
    assertEquals("approver456", dto.getApproverId());
  }

  @Test
  void shouldThrowExceptionIfDecisionNotFound() {
    // Given
    UUID appId = UUID.randomUUID();
    when(decisionRepository.findByApplicationId(appId)).thenReturn(Optional.empty());

    // Expect
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          decisionService.getDecisionByApplicationId(appId.toString());
        });

    verify(decisionRepository).findByApplicationId(appId);
  }
}
